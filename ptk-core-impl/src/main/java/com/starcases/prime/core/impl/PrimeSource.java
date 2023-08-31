package com.starcases.prime.core.impl;

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
import java.util.concurrent.atomic.AtomicBoolean;

import com.starcases.prime.base.api.BaseGenIntfc;
import com.starcases.prime.cache.api.PersistedCacheIntfc;
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
import com.starcases.prime.metrics.api.MetricProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import jakarta.validation.constraints.Min;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongLongHashMap;
import org.eclipse.collections.impl.parallel.ParallelIterate;

/**
 * Provides data structure holding the core data and objects that provide
 * access to the data from the
 * "public interface for primes". Also some utility support to support
 *  alternative algs for finding primes, working with bases of the primes,
 *  and general information/access.
 *
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals"})
public class PrimeSource implements PrimeSourceFactoryIntfc
{
	private static final long serialVersionUID = 1L;

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

	//
	// Metrics / health / progress
	//
	@Getter
	private final MetricProviderIntfc metricProvider;

	/**
	 * dest of output progress tracking
	 * during initial base creation
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private ProgressIntfc progress;

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

	//
	// Internal data used/generated during prime/base creation
	//

	private List<BaseGenIntfc> baseGenerators = Lists.mutable.empty();

	/**
	 *  map prime to long index
	 */
	@Getter(AccessLevel.PRIVATE)
	private final MutableLongLongMap primeToIdxMap;

	private final PersistedCacheIntfc<Long> primeCache;

	/**
	 * Multi-level container for the tree of primes.
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
			final PersistedCacheIntfc<Long> primeCache,
			final MetricProviderIntfc metricProvider
			)
	{
		super();

		this.collTracker = collTracker;
		this.primeCache = primeCache;
		final int capacity = 50_500_000;
		primeToIdxMap = new LongLongHashMap(capacity);

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

		this.metricProvider = metricProvider;
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
		final PrimeRefFactoryIntfc ret = primeRefRawCtor.apply(nextPrimeIdx);

		updateMaps(nextPrimeIdx, newPrime, ret);
		ret.generateBases(getBasesGenerator());

		return ret;
	}


	private Consumer<PrimeRefFactoryIntfc> getBasesGenerator()
	{
		return (pRef) -> ParallelIterate.forEach(
							baseGenerators,
							gen ->
								{
									gen.genBasesForPrimeRef(pRef);
								});
	}

	@Override
	public OptionalLong getPrimeForIdx(@Min(0) final long primeIdx)
	{
		final long [] retSubset = {-1};
		final int [] retOffset = {-1};
		idxMap.convertIdxToSubsetAndOffset(primeIdx, retSubset, retOffset);

		final var val = this.primeCache.get(retSubset[0]);
		final var prime = val.get(retOffset[0]);
		return val == null ? OptionalLong.empty() : OptionalLong.of(prime);
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForIdx(@Min(0) final long primeIdx)
	{
		final long [] retSubset = {-1};
		final int [] retOffset = {-1};
		idxMap.convertIdxToSubsetAndOffset(primeIdx, retSubset, retOffset);

		final var val = this.primeCache.get(retSubset[0]);
		Optional<PrimeRefIntfc> ret = Optional.empty();
		if (retSubset[0] <= val.getMaxOffsetAssigned())
		{
			final var prime = val.get(retOffset[0]);
			ret = val == null ? Optional.empty() : prime == null ? Optional.empty() : Optional.of(new PrimeRef(primeIdx));
		}
		else
		{
			System.out.println(String.format("primeIdx overflow: prime-idx %d subset %d offset %d maxassigned-idx %d",  primeIdx, retSubset[0], retOffset[0], val.getMaxOffsetAssigned()));
		}
		return ret;
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForPrime(@Min(0) final long prime)
	{
		return this.getPrimeRefForIdx(primeToIdxMap.get(prime));
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForPrime(@NonNull final LongSupplier longSupplier)
	{
		return this.getPrimeRefForIdx(primeToIdxMap.get(longSupplier.getAsLong()));
	}

	@Override
	public Iterator<PrimeRefIntfc> getPrimeRefIter()
	{
		var ret = new PrimeRefIterator(new PrimeRef(-1));
		return ret;
	}

	@Override
	public Stream<PrimeRefIntfc> getPrimeRefStream(final boolean preferParallel)
	{
		return getPrimeRefStream(0, preferParallel);
	}

	@Override
	public Stream<PrimeRefIntfc> getPrimeRefStream(@Min(1) final long skipCount, final boolean preferParallel)
	{
		final Iterator<PrimeRefIntfc> iter = getPrimeRefIter();
		final Supplier<PrimeRefIntfc> supplier = () -> { var it = iter; return it.hasNext() ? ((Iterator<PrimeRefIntfc>)it).next() : null; };
		return Stream.generate(supplier).takeWhile( p -> Objects.nonNull(p)).skip(skipCount);
	}

	@Override
	public void init()
	{
		if (doInit.compareAndExchangeAcquire(false, true))
		{
			// Prevent double init - various valid combinations of code can attempt that.
			return;
		}

		if (displayPrimeTreeMetrics)
		{
			collTracker.log();
		}
	}

	@Override
	public void setDisplayDefaultBaseMetrics(final boolean display)
	{
		this.displayPrimeTreeMetrics = display;
	}

	/**
	 * Provide class instance that manages progress information
	 * for base prime generation.
	 */
	@Override
	public void setDisplayProgress(@NonNull final ProgressIntfc progress)
	{
		this.progress = progress;
	}

	private void updateMaps(@Min(0) final long idx, @Min(1) final long newPrime, @NonNull final PrimeRefIntfc ref)
	{
		this.primeToIdxMap.put(newPrime, idx);
	}

	@Override
	public void addBaseGenerator(final BaseGenIntfc baseGenerator)
	{
		baseGenerators.add(baseGenerator);
	}
}
