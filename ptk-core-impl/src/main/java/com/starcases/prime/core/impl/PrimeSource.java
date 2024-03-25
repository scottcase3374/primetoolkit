package com.starcases.prime.core.impl;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.starcases.prime.base.api.BaseGenIntfc;
import com.starcases.prime.cache.api.PersistedCacheIntfc;
import com.starcases.prime.cache.api.PersistedPrefixCacheIntfc;
import com.starcases.prime.cache.api.persistload.PersistLoaderIntfc;
import com.starcases.prime.cache.api.subset.SubsetIntfc;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.api.ProgressIntfc;
import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.datamgmt.impl.PrimeRefIterator;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.kern.impl.IdxToSubsetMapperImpl;
import com.starcases.prime.kern.api.IdxToSubsetMapperIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import jakarta.validation.constraints.Min;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

/**
 * Provides data structure holding the core data and objects that provide
 * access to the data from the
 * "public interface for primes". Also some utility support to support
 *  alternative algs for finding primes, working with bases of the primes,
 *  and general information/access.
 *
 */
public class PrimeSource implements PrimeSourceFactoryIntfc
{
	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
	/**
	 * default logger
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final Logger LOG = Logger.getLogger(PrimeSource.class.getName());

	//
	// Flags conveyed into this class
	//

	/**
	 * atomic flag preventing duplicate init
	 */
	@Getter(AccessLevel.PRIVATE)
	private final AtomicBoolean doInit = new AtomicBoolean(false);

	/**
	 * Flag indicating whether to currently log mismatch between prime/base
	 * found and the next actual prime in the sequence.
	 */
	@Getter(AccessLevel.PRIVATE)
	private final AtomicBoolean logMismatch = new AtomicBoolean(true);

	/**
	 * flag indicating whether to output prefix/tree
	 * metrics from initial prime creation
	 */
	@Getter(AccessLevel.PRIVATE)
	private boolean displayPrimeTreeMetrics;

	/**
	 * dest of output progress tracking
	 * during initial base creation
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private ProgressIntfc progress;

	@Getter
	@Setter
	private boolean createBases;

	//
	// Context type info/code for prime/base creation that is conveyed into
	// this class
	//

	/**
	 * number of base primes to generate
	 */
	@Getter(AccessLevel.PRIVATE)
	private final long targetPrimeCount;

	/** can be overridden by passing
	* an appropriate "new" function ptr into the PrimeSource
	* constructor  -> param primeRefCtor
	*/
	@Getter(AccessLevel.PRIVATE)
	private final Function<Long, PrimeRefFactoryIntfc> primeRefRawCtor;

	@Setter
	PersistLoaderIntfc prefixLoader;

	//
	// Internal data used/generated during prime/base creation
	//

	private List<BaseGenIntfc> baseGenerators = Lists.mutable.empty();

	private DB db = DBMaker
		    .fileDB("ptk.db")
		    .fileMmapEnable()            // Always enable mmap
		    .fileMmapPreclearDisable()   // Make mmap file faster
		    .allocateStartSize(5L * 1024 * 1024 * 1024) // 5 GB
		    .allocateIncrement(1024L * 1024 * 1024) // 1 GB
		    .checksumHeaderBypass()
		    .make();

	private record SubsetOffsetMap(long subset, int offset) implements Serializable {}
	private final ConcurrentMap<Long, SubsetOffsetMap> primeToSubsetOffsetMap;

	private final HTreeMap<Long, Long> primeMap;
	private final HTreeMap<Long, long[]> primeBaseMap = db.hashMap("some_other_map", Serializer.LONG, Serializer.LONG_ARRAY).createOrOpen();

	// Lookup primes by subset/ offset which can be calculated from the index.
	// Cache use is intended to allow only storing a limited number of primes
	// in-memory at a particular point in time.
	private final PersistedCacheIntfc<Long,Long> primeCache;

	private final PersistedPrefixCacheIntfc prefixCache;

	/**
	 * Multi-level container for the tree of primes - all in memory.
	 */
	@Getter(AccessLevel.PRIVATE)
	private CollectionTrackerIntfc collTracker;


	private static final IdxToSubsetMapperIntfc idxMap = new IdxToSubsetMapperImpl();

	//
	// initialization
	//

	/**
	 * primary constructor of prime source - for lookups of prime/prime refs.
	 * @param maxCount
	 * @param consumersSetPrimeSrc
	 * @param confidenceLevel
	 * @param primeRefRawCtor
	 * @param collTrack
	 */
	public PrimeSource(
			@Min(1) final long maxCount,
			@NonNull final ImmutableList<Consumer<PrimeSourceIntfc>> consumersSetPrimeSrc,
			@NonNull final Function<Long, PrimeRefFactoryIntfc> primeRefRawCtor,
			final CollectionTrackerIntfc collTracker,
			final PersistedCacheIntfc<Long,Long> primeCache,
			final PersistedPrefixCacheIntfc prefixCache
			)
	{
		super();

		this.collTracker = collTracker;
		this.primeCache = primeCache;
		this.prefixCache = prefixCache;

		primeToSubsetOffsetMap = db.hashMap("primeToSubsetOffsetMap", Serializer.LONG, Serializer.JAVA).createOrOpen();

		targetPrimeCount = maxCount;

		this.primeRefRawCtor = primeRefRawCtor;
		consumersSetPrimeSrc
			.forEach(c ->
					{
						if (c != null)
						{
							c.accept(this);
						}
					}
						);
	}

	/**
	 * Add new Prime to shared set of all primes
	 *
	 * @param aPrime
	 */
	@Override
	public PrimeRefFactoryIntfc addPrimeRef(
			@Min(0) final long nextPrimeIdx,
			@Min(1) final long newPrime
			)
	{
		final long [] subset = {-1};
		final int [] offset = {-1};

		PrimeSource.idxMap.convertIdxToSubsetAndOffset(nextPrimeIdx, subset, offset);
		return addPrimeRef(subset[0], offset[0], newPrime);
	}

	@Override
	public PrimeRefFactoryIntfc addPrimeRef(
			@Min(0) final long primeSubset,
			@Min(0) final int primeOffset,
			@Min(1) final long newPrime
			)
	{
		final long primeIdx = primeSubset * IdxToSubsetMapperIntfc.SUBSET_SIZE + primeOffset;
		final PrimeRefFactoryIntfc ret = primeRefRawCtor.apply(primeIdx);

		updateMaps(primeSubset, primeOffset, newPrime);
		ret.generateBases(getBasesGenerator());

		return ret;
	}


	private Consumer<PrimeRefFactoryIntfc> getBasesGenerator()
	{
		return pRef -> ParallelIterate.forEach(
							baseGenerators,
							gen -> gen.genBasesForPrimeRef(pRef) );
	}

	@Override
	public OptionalLong getPrimeForIdx(@Min(0) final long primeIdx)
	{
		final long [] retSubset = {-1};
		final int [] retOffset = {-1};
		idxMap.convertIdxToSubsetAndOffset(primeIdx, retSubset, retOffset);

		return getPrimeForIdx(retSubset[0], retOffset[0]);
	}

	@Override
	public OptionalLong getPrimeForIdx(@Min(0) final long primeSubset, @Min(0) final int primeOffset)
	{
		final var val = this.primeCache.get(primeSubset);
		final var prime = val.get(primeOffset);
		return OptionalLong.of(prime);
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForIdx(@Min(0) final long primeIdx)
	{
		final long [] retSubset = {-1};
		final int [] retOffset = {-1};
		idxMap.convertIdxToSubsetAndOffset(primeIdx, retSubset, retOffset);

		return getPrimeRefForIdx(retSubset[0], retOffset[0]);
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForIdx(@Min(0) final long primeSubset, @Min(0) final int primeOffset)
	{
		final Optional [] ret = { Optional.empty() };

		final var valOpt = Optional.ofNullable(this.primeCache.get(primeSubset));
		valOpt.ifPresent( val ->
				{
					if (primeSubset <= val.getMaxOffsetAssigned())
					{
						final var prime = val.get(primeOffset);
						ret[0] = prime == null ? Optional.empty() : Optional.of(new PrimeRef(primeSubset, primeOffset));
					}
					else
					{
						statusHandler.output("primeIdx overflow: subset %d offset %d maxassigned-idx %d", primeSubset, primeOffset, val.getMaxOffsetAssigned());
				}});

		return ret[0];
	}

	/**
	 * Get highest prime ref less than specified value and prime.
	 *
	 */
	@Override
	public Optional<PrimeRefIntfc> getPrimeRefCeiling(final long value, @NonNull final PrimeRefIntfc highPrime)
	{
		if (value > highPrime.getPrime())
		{
			return Optional.empty();
		}

		Optional<PrimeRefIntfc> prime = getPrimeRefForPrime(value);
		if (prime.isEmpty())
		{
			Optional<PrimeRefIntfc> tmpPrime = highPrime.getPrevPrimeRef();
			while( tmpPrime.isPresent() && value > tmpPrime.get().getPrime())
			{
				tmpPrime = tmpPrime.get().getPrevPrimeRef();
			}
			prime = tmpPrime;
		}

		return prime;
	}

	/**
	 * Similar result to getPrimeRefCeiling but result is represented
	 * differently. Offset is either positive meaning it is the
	 * index of the searched value (which would be prime in this case)
	 * or the index is negative which represents -(insertion index -1).
	 * See Arrays.binarySearch for further details related to the offset
	 * value here.
	 *
	 * @param val
	 * @return
	 */
	public SearchResult searchPrime(final long val)
	{
		SearchResult searchResult = null;

		long subsetIdx = -1;
		int idx = -1;
		SubsetIntfc<Long> subset;
		while ((subset = primeCache.get(++subsetIdx)) != null)
		{
			final long curMax = subset.get(subset.getMaxOffset());
			if ( val <= curMax )
			{
				idx = Arrays.binarySearch(subset.getEntries(), val);
				searchResult = new SearchResult(subsetIdx, idx);
				break;
			}
		}
		return searchResult;
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForPrime(@Min(0) final long prime)
	{
		final var subsetAndOffset = primeToSubsetOffsetMap.get(prime);
		return  subsetAndOffset == null ? Optional.empty() : getPrimeRefForIdx(subsetAndOffset.subset, subsetAndOffset.offset);
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForPrime(@NonNull final LongSupplier longSupplier)
	{
		final long prime = longSupplier.getAsLong();
		final var subsetAndOffset = primeToSubsetOffsetMap.get(prime);
		return this.getPrimeRefForIdx(subsetAndOffset.subset, subsetAndOffset.offset);
	}

	@Override
	public Iterator<PrimeRefIntfc> getPrimeRefIter()
	{
		return new PrimeRefIterator(new PrimeRef(0,0));
	}

	@Override
	public Iterator<PrimeRefIntfc> getPrimeRefIter(final long idx)
	{
		return new PrimeRefIterator(new PrimeRef(idx));
	}

	@Override
	public Iterator<PrimeRefIntfc> getPrimeRefIter(@Min(0) final long subset, @Min(0) final int offset)
	{
		return new PrimeRefIterator(new PrimeRef(subset, offset));
	}

	@Override
	public Stream<PrimeRefIntfc> getPrimeRefStream(final boolean preferParallel)
	{
		return getPrimeRefStream(0, preferParallel);
	}

	@Override
	public Stream<PrimeRefIntfc> getPrimeRefStream(@Min(1) final long skipCount, final boolean preferParallel)
	{
		final Iterator<PrimeRefIntfc> iter = getPrimeRefIter(skipCount-1);
		final Supplier<PrimeRefIntfc> supplier = () -> { var it = iter; return it.hasNext() ? it.next() : null; };
		return Stream.generate(supplier).takeWhile( Objects::nonNull);
	}

	@Override
	public void init()
	{
		if (doInit.compareAndExchangeAcquire(false, true))
		{
			// Prevent double init - various valid combinations of code can attempt that.
			return;
		}

		// Random choice of index to check for existence of map entries
		// created at a prior time.
		var mapsUninitialized = primeToSubsetOffsetMap.get(17L) == null;

		getPrimeRefStream(true).forEach
			(
				pRef ->
					{
						final long index = pRef.getPrimeRefIdx();

						if (mapsUninitialized)
						{
							this.updateMaps(index, pRef.getPrime());
						}

						if (index % 10000 == 0)
						{
							statusHandler.dbgOutput("PrimeSource::init - updated maps for index %d", index);
						}

						if (createBases)
						{
							final var baseData = pRef.getPrimeBaseData();
							if (baseData == null || baseData.getPrimeBases().isEmpty())
							{
								this.generateBases(index);
							}
						}
					}
			);

		if (!createBases && prefixLoader != null)
		{
			prefixLoader.process();
		}
	}

	public void updateMaps(@Min(0) final long index, @Min(1) final long prime)
	{
		final long [] retSubset = {-1};
		final int [] retOffset = {-1};
		idxMap.convertIdxToSubsetAndOffset(index, retSubset, retOffset);
		updateMaps(retSubset[0], retOffset[0], prime);
	}

	private void updateMaps(@Min(0) final long subset, @Min(0) final int offset, @Min(1) final long newPrime)
	{
		primeToSubsetOffsetMap.put(newPrime, new SubsetOffsetMap(subset, offset));
		db.commit();
	}

	@Override
	public void addBaseGenerator(final BaseGenIntfc baseGenerator)
	{
		statusHandler.output("adding baseGenerator %s ", baseGenerator.getBaseType());
		baseGenerators.add(baseGenerator);
	}

	@Override
	public void generateBases(final @Min(0) long primeSubset, final @Min(0) int primeOffset)
	{
		final var pRef = this.getPrimeRefForIdx(primeSubset, primeOffset);
		pRef.ifPresent(ref -> baseGenerators.forEach(bGen -> bGen.genBasesForPrimeRef(ref)) );

		if (primeOffset % 10000 == 0)
		{
			statusHandler.dbgOutput("baseSubset %d offset %d at %s", primeSubset, primeOffset, LocalTime.now().toString());
		}
	}

	@Override
	public void generateBases(final @Min(0) long index)
	{
		final long [] retSubset = {-1};
		final int [] retOffset = {-1};
		idxMap.convertIdxToSubsetAndOffset(index, retSubset, retOffset);
		generateBases(retSubset[0], retOffset[0]);
	}
}
