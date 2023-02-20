package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.concurrent.atomic.AtomicBoolean;

import com.codahale.metrics.Timer;
import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.base.primetree.PrimeTree;
import com.starcases.prime.cli.OutputOper;
import com.starcases.prime.intfc.CollectionTrackerIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.metrics.MetricMonitor;
import com.starcases.prime.preload.PrePrimed;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import jakarta.validation.constraints.Min;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.LongLongHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;
import org.eclipse.collections.impl.set.immutable.primitive.ImmutableLongSetFactoryImpl;

/**
 * Provides data structure holding the core data and objects that provide
 * access to the data from the
 * "public interface for primes". Also some utility support to support
 *  alternative algs for finding primes, working with bases of the primes,
 *  and general information/access.
 *
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals"})
public class PrimeSource extends AbstractPrimeBaseGenerator implements PrimeSourceIntfc
{
	private static final long serialVersionUID = 1L;

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

	/**
	 * dest of output progress tracking
	 * during initial base creation
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private GenerationProgress progress;

	//
	// Context type info/code for prime/base creation that is conveyed into
	// this class
	//

	/**
	 * confidence level for check of prime
	 */
	@Getter(AccessLevel.PRIVATE)
	private final int confidenceLevel;

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
	private final BiFunction<Long, MutableList<ImmutableLongCollection>, PrimeRefIntfc> primeRefRawCtor;

	/**
	 * Container of pre-generated prime/base data.
	 */
	@Getter(AccessLevel.PRIVATE)
	private PrePrimed prePrimed;

	/**
	 * single-level structure to manage a unique references to each unique
	 *  collection of primes. Structure is populated during base/prime
	 *  creation.
	 */
	@Getter(AccessLevel.PRIVATE)
	private final CollectionTrackerIntfc collTrack;

	//
	// Internal data used/generated during prime/base creation
	//

	/**
	 * atomic long for next prime index.
	 */
	@Getter(AccessLevel.PRIVATE)
	private final AtomicLong nextIdx = new AtomicLong(1);

	/**
	 *  map prime to long index
	 */
	@Getter(AccessLevel.PRIVATE)
	private final MutableLongLongMap primeToIdxMap;

	/** map long idx to PrimeMapEntry,  PrimeMapEntry is primeRef and BigInt
	* long idx provides order to primerefs and therefore indirectly to the primes
	*/
	@Getter(AccessLevel.PRIVATE)
	private final MutableLongObjectMap<PrimeMapEntry> idxToPrimeMap;

	/**
	 * Multi-level container for the tree of primes.
	 */
	@Getter(AccessLevel.PRIVATE)
	private PrimeTree primeTree;

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
			@Min(1) final int confidenceLevel,
			@NonNull final BiFunction<Long, MutableList<ImmutableLongCollection>, PrimeRefIntfc> primeRefRawCtor,
			final CollectionTrackerIntfc collTrack
			)
	{
		super();

		// yes, this seems odd - maybe refactor later.
		super.primeSrc = this;

		this.collTrack = collTrack;

		final int capacity = (int)(maxCount*1.25);
		idxToPrimeMap = new LongObjectHashMap<>(capacity);
		primeToIdxMap = new LongLongHashMap(capacity);

		targetPrimeCount = maxCount;

		this.primeRefRawCtor = primeRefRawCtor;
		consumersSetPrimeSrc.forEach(c -> c.accept(this));

		addPrimeRef(0L, 1L, MutableListFactoryImpl.INSTANCE.of(ImmutableLongSetFactoryImpl.INSTANCE.with(1L), ImmutableLongSetFactoryImpl.INSTANCE.with(0L)));
		addPrimeRef(1L, 2L, MutableListFactoryImpl.INSTANCE.of(ImmutableLongSetFactoryImpl.INSTANCE.with(2L), ImmutableLongSetFactoryImpl.INSTANCE.with(0L)));

		this.confidenceLevel = confidenceLevel;
	}

	/**
	 * Add new Prime to shared set of all primes
	 *
	 * @param aPrime
	 */
	private void addPrimeRef(
			@Min(0) final long nextPrimeIdx,
			@Min(1) final long newPrime,
			final MutableList<ImmutableLongCollection> baseSets
			)
	{
		final PrimeRefIntfc ret = primeRefRawCtor.apply(Long.valueOf(nextPrimeIdx), baseSets);

		checkMismatch("addPrimeRef", nextPrimeIdx, newPrime);

		updateMaps(nextPrimeIdx, newPrime, ret);
	}

	private long calcSumCeiling(@Min(3) final long primeSum)
	{
		return (primeSum << 1) - 1L;
	}

	/**
	 * Data quality check; confirm that the identified prime/base is the expected value
	 * when pre-determined values exists. This only logs a message on the first
	 * mismatch.
	 *
	 * @param src
	 * @param idx
	 * @param newPrime
	 */
	private void checkMismatch(final String src, final long idx, final long newPrime)
	{
		if (prePrimed != null)
		{
			final long expectedPrime = prePrimed.retrieve((int)idx);

			if (newPrime != expectedPrime && LOG.isLoggable(Level.SEVERE) && logMismatch.compareAndSet(true, false) )
			{
				LOG.severe(String.format("Mismatch: [%s]  idx [%d] newPrime[%d] pre-primed@idx [%d]", src,  idx, newPrime, expectedPrime));
			}
		}
	}

	/**
	 * This is the "prime" logic so to speak..  the "innovative" side of
	 * this isn't the initial identification of primes but the data that
	 * is created related to each prime - the "bases".  The questions to
	 * ask are - if given some items representing each prime; is there
	 * an identifiable pattern over any range of primes that provides
	 * insights into faster identification of much larger primes? Can
	 * something new be learned with regard to factors of numbers created
	 * from large primes?
	 */
	@Override
	protected void genBasesImpl()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("PrimeSource::genBasesImpl()");
		}

		primeTree = new PrimeTree(this, collTrack);

		MetricMonitor.addTimer(OutputOper.CREATE,"Gen Bases");

		long nextPrimeTmpIdx;
		do
		{
			try (Timer.Context context = MetricMonitor.time(OutputOper.CREATE).orElse(null))
			{
				final int nextPrimeIdx = (int) nextIdx.incrementAndGet();
				final var lastPrimeIdx = nextPrimeIdx -1;
				final var lastPrime = getPrimeForIdx(lastPrimeIdx);

				if (!genByListPermutation(nextPrimeIdx, lastPrime) && !genByPrimePermutation(nextPrimeIdx, lastPrime))
				{
					LOG.severe("No prime found");
					throw new IllegalStateException("No prime found");
				}

				if (null != progress)
				{
					progress.dispProgress(nextPrimeIdx);
				}

				nextPrimeTmpIdx = nextPrimeIdx;
			}
		}
		while (nextPrimeTmpIdx < targetPrimeCount);

		if (this.prePrimed != null)
		{
			this.prePrimed.dumpStats();
		}
	}

	/**
	 * Generation of prime/bases through permutations of existing prime base lists.
	 * The idea behind this is that using lists as determined for past items has
	 * potential to be faster than iterating through powersets of individual
	 * past bases/primes. This won't always work since a new list is sometimes
	 * needed but since the same lists occur for many items this reduces the
	 * processing need to an extent.
	 *
	 * @param optCurPrime
	 * @return
	 */
	private boolean genByListPermutation(final int nextPrimeIdx, final OptionalLong optCurPrime)
	{
		boolean [] found = {false};
		optCurPrime.ifPresent(curPrime ->
						{
							// NOTE: Selection process must pick lowest item
							final Optional<PData> pdata = primeTree.select(
									primeCollSum ->
									{
										final long possibleNextPrime = curPrime + primeCollSum;
										return BigInteger.valueOf(possibleNextPrime).isProbablePrime(100);
										});

							pdata.ifPresent( pd ->
										{
											final var nextPrime = curPrime + pd.prime();
											found[0] = true;

											checkMismatch("genByListPermutation", nextPrimeIdx, nextPrime);

											addPrimeRef(nextPrimeIdx, nextPrime, MutableListFactoryImpl.INSTANCE.of(pd.toCanonicalCollection(), ImmutableLongSetFactoryImpl.INSTANCE.with(curPrime)));
										}
									);
							});

		return found[0];
	}

	/**
	 * At the moment, this is only public because I want to ensure
	 * this logic is correct through some unit tests.  This is
	 * fundamentally calculating log-base-2 for the value. Slightly
	 * different implementations can have different performance
	 * impacts. This can be called a very large number of times
	 * in total so ability to tune it may be useful.
	 *
	 * @param value
	 * @return
	 */
	public int getBitsRequired(@Min(0) final long value)
	{
		final long [] masks = { 0xFFL, 0xFF00L, 0xFF000000L,  0xFF00000000000000L };
		final int [] bitStart = {7, 15, 31, 63};
		final int [] bitEnd = {0, 8, 16, 32};
		int ret = 1;
		for (int idx = masks.length-1; idx >= 0 ; idx--)
		{
			if ((value & masks[idx]) > 0)
			{
				for(int bit = bitStart[idx] ; bit > bitEnd[idx]; bit--)
				{
					if ( (value & (1<<bit)) > 0)
					{
						ret = bit+1;
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Generate primes/bases through permutations of existing primes/bases.
	 * @param optCurPrime
	 * @return
	 */
	private boolean genByPrimePermutation(final int idx, final OptionalLong optCurPrime)
	{
		// Represents a X-bit search space of indexes for primes to add for next Prime.
		final var numBitsForPrimeCount =  getBitsRequired(optCurPrime.getAsLong());
		final var primeIndexMaxPermutation = new BitSet();
		primeIndexMaxPermutation.set(numBitsForPrimeCount); // keep 'shifting' max bit left

		// no reason to perform work when no indexes
		// selected so start with 1 index selected.
		final var primeIndexPermutation = new BitSet();
		primeIndexPermutation.set(0);

		final var sumCeiling = optCurPrime.stream().map(this::calcSumCeiling).reduce(0L, (a,b) -> a+b);

		boolean foundPrime = false;

		var doLoop = !(primeIndexPermutation.equals(primeIndexMaxPermutation));
		while (doLoop)
		{
			final long permutationSum = primeIndexPermutation
					.stream()
					.mapToObj(this::getPrimeForIdx)
					.filter(OptionalLong::isPresent)
					.map(OptionalLong::getAsLong)
					.reduce(optCurPrime.orElse(0), (a, b) -> a+b);

			// if exceed known prime then iteration is done - limits useless work
			doLoop = permutationSum - sumCeiling <= 0;
			if (doLoop)
			{
				final var optCurPrimeVal = optCurPrime.getAsLong();
				if (optCurPrime.isPresent() && viablePrime(permutationSum, optCurPrimeVal))
				{
					final var prefixTree = primeTree.iterator();
					primeIndexPermutation.stream().forEach(prefixIdx -> getPrimeForIdx(prefixIdx).ifPresent(prefixTree::add) );

					final var primeTreeColl = prefixTree.toCollection();
					final var canonicalPrimeColl = collTrack.track(primeTreeColl).toCanonicalCollection();

					checkMismatch("genByPrimePermutation", idx, permutationSum);

					addPrimeRef(idx, permutationSum, MutableListFactoryImpl.INSTANCE.of(canonicalPrimeColl, ImmutableLongSetFactoryImpl.INSTANCE.with(optCurPrimeVal)));
					doLoop = false;
					foundPrime = true;
				}
				else
				{
					incrementPermutation(primeIndexPermutation);
				}
			}
		}
		return foundPrime;
	}

	@Override
	public OptionalLong getPrimeForIdx(@Min(0) final long primeIdx)
	{
		final var ret = idxToPrimeMap.get(primeIdx);
		return ret != null ? OptionalLong.of(ret.getPrime()) : OptionalLong.empty();
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForIdx(@Min(0) final long primeIdx)
	{
		final var primeRef = idxToPrimeMap.get(primeIdx).getPrimeRef();
		return Optional.of(primeRef);
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForPrime(@Min(0) final long prime)
	{
		return this.getPrimeRefForIdx(primeToIdxMap.get(prime));
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForPrime(LongSupplier longSupplier)
	{
		return this.getPrimeRefForIdx(primeToIdxMap.get(longSupplier.getAsLong()));
	}

	@Override
	public java.util.Iterator<PrimeRefIntfc> getPrimeRefIter()
	{
		return new PrimeMapIterator(idxToPrimeMap.iterator());
	}

	@Override
	public Stream<PrimeRefIntfc> getPrimeRefStream(final boolean preferParallel)
	{
		return (preferParallel ? idxToPrimeMap.values().parallelStream() : idxToPrimeMap.values().stream()).map(PrimeMapEntry::getPrimeRef);
	}

	@Override
	public Stream<PrimeRefIntfc> getPrimeRefStream(final long skipCount, final boolean preferParallel)
	{
		return (preferParallel ? idxToPrimeMap.values().stream().skip(skipCount).parallel() : idxToPrimeMap.values().stream().skip(skipCount)).map(PrimeMapEntry::getPrimeRef);
	}

	private void incrementPermutation(@NonNull final BitSet primePermutation)
	{
		var bit = 0;
		// Generate next permutation of the bit indexes
		for(;;)
		{
			// performs add and carry if needed
			primePermutation.flip(bit);

			if (primePermutation.get(bit++)) // true means no carry
			{
				break;
			}
		}
	}

	@Override
	public void init()
	{
		if (doInit.compareAndExchangeAcquire(false, true))
		{
			// Prevent double init - various valid combinations of code can attempt that.
			return;
		}

		genBases();

		if (displayPrimeTreeMetrics)
		{
			collTrack.log();
		}
	}

	@Override
	public void load(final PrePrimed prePrimed, final BaseTypes ... baseTypes)
	{
		if (doInit.getPlain())
		{

		}
		this.prePrimed = prePrimed;
		//Map<Integer, BigInteger>  prCache = cacheMgr.getCache("primes");
		//if (!prCache.isEmpty())
		//	{
		//	primes.putAll(prCache);
		//	primes.forEach((k, v) -> addPrimeRef(v, Collections.emptyList()));
		//	LOG.info(String.format("cached entries loaded: %d ", primes.size()));
		//}
		//else
		//{
		//	LOG.info("cache empty");
		//}
	}

	@Override
	public void setDisplayPrimeTreeMetrics(final boolean display)
	{
		this.displayPrimeTreeMetrics = display;
	}

	/**
	 * Provide class instance that manages progress information
	 * for base prime generation.
	 */
	@Override
	public void setDisplayProgress(final GenerationProgress progress)
	{
		this.progress = progress;
	}

	/**
	 * Flags for bases to store in disk cache.
	 */
	@Override
	public void store(final BaseTypes ... baseTypes)
	{
		//	cacheMgr.getCache("primes").putAll(primes);
		//if (baseTypes != null)
		//	cacheMgr.getCache(BaseTypes.DEFAULT.name()).putAll(primeRefs.get(BaseTypes.DEFAULT));
	}

	private void updateMaps(final long idx, final long newPrime, final PrimeRefIntfc ref)
	{
		this.idxToPrimeMap.put(idx, new PrimeMapEntry(newPrime, ref));
		this.primeToIdxMap.put(newPrime, idx);
	}

	/**
	 *
	 * Weed out sums which cannot represent the next Prime.
	 *
	 * @param sumOfPrimeSet
	 * @return true for sum that is viable Prime; false otherwise
	 */
	private boolean viablePrime(@Min(1) final long primeSum, @Min(1) final long lastMaxPrime)
	{
		return
			// Part of "non-cheating" Prime checks.
			// only want sets summing to greater than the current
			// max Prime.
				(primeSum - lastMaxPrime) > 0
			&&
			// Part of "non-cheating" Prime checks.
			// Not a Prime if is even.
				(primeSum & 1) == 1
			&&
				// This is bootstrap logic.
				// If possible, this block should be removed and allow the
				// remaining blocks to determine next Prime.
				BigInteger.valueOf(primeSum).isProbablePrime(confidenceLevel);
	}
}
