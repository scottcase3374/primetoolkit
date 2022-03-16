package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import java.util.Iterator;
import java.util.List;

import lombok.NonNull;
import javax.validation.constraints.Min;

import org.infinispan.manager.EmbeddedCacheManager;

/**
 * Provides data structure holding the core data and objects that provide access to the data from the
 * "public interface for primes". Also some utility support to support alternative algs for
 * finding primes, working with bases of the primes, and general information/access.
 *
 */
public class PrimeSource implements PrimeSourceIntfc
{
	private static final Logger log = Logger.getLogger(PrimeSource.class.getName());
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private transient EmbeddedCacheManager cacheMgr;

	@Min(1)
	private final int confidenceLevel;

	@Min(-1)
	private final AtomicInteger nextIdx = new AtomicInteger(-1);

	@NonNull
	private final List<List<Integer>> prefixes = new ArrayList<>();

	@NonNull
	private final Map<Integer, BigInteger> primes;

	@NonNull
	private final Map<Integer,PrimeRefIntfc> primeRefs;


	@Min(1)
	private final int targetPrimeCount;

	@NonNull
	private BaseTypes activeBaseId = BaseTypes.DEFAULT;

	@NonNull
	private final AtomicBoolean doInit = new AtomicBoolean(false);

	// Default PrimeRef implementation to use for this PrimeSource.
	//
	// This is non-static because it currently can be overridden by passing
	// an appropriate "new" function ptr into the PrimeSource
	// constructor  -> param primeRefCtor
	//
	// Currently no plan to allow changing primeRefCtor after initial PrimeSource
	// creation - should have a static setter if I want to move the override
	// of this out of the constructor.
	//
	// This does seem a bit hacky - may revisit later.
	@NonNull
	private transient BiFunction<Integer, Set<Integer>, PrimeRefIntfc> primeRefCtor;

	private transient Consumer<PrimeSourceIntfc> baseSetPrimeSrc;

	//
	// initialization
	//

	public PrimeSource(
			@Min(1) int maxCount,
			@NonNull BiFunction<Integer, Set<Integer>, PrimeRefIntfc> primeRefCtor,
			@NonNull Consumer<PrimeSourceIntfc> consumerSetPrimeSrc,
			@NonNull Consumer<PrimeSourceIntfc> baseSetPrimeSrc,
			@Min(1) int confidenceLevel,
			EmbeddedCacheManager cacheMgr
			)
	{

		this.cacheMgr = cacheMgr;
		primes = new ConcurrentHashMap<>(maxCount);
		primeRefs = new ConcurrentHashMap<>(maxCount);
		targetPrimeCount = maxCount;

		this.primeRefCtor = primeRefCtor;
		consumerSetPrimeSrc.accept(this);
		this.baseSetPrimeSrc = baseSetPrimeSrc;
		baseSetPrimeSrc.accept(this);

		var tmpIndexSet = new TreeSet<Integer>();
		tmpIndexSet.add(0);
		addPrimeRef(BigInteger.valueOf(1L), tmpIndexSet);

		tmpIndexSet = new TreeSet<Integer>();
		tmpIndexSet.add(1);
		addPrimeRef(BigInteger.valueOf(2L), tmpIndexSet);

		this.confidenceLevel = confidenceLevel;
	}

	@Override
	public void init()
	{
		if (doInit.compareAndExchangeAcquire(false, true))
		{
			return;
		}

		log.entering("PrimeSource", "init()");

		final var primeIndexMaxPermutation = new BitSet();
		final var primeIndexPermutation = new BitSet();

		// Used for informative purposes - like a progress meter.
		long primesProcessed1k = 0;

		var allPrimesProcessed = false;

		// each iteration increases the #bits by 1; a new Prime is determined per iteration
		do
		{
			final var curPrimeIdx = nextIdx.get();
			final var curPrime = getPrime(curPrimeIdx);

			// Represents a X-bit search space of indexes for primes to add for next Prime.
			final var numBitsForPrimeCount = primeRefs.size()-1;
			primeIndexMaxPermutation.clear(numBitsForPrimeCount-1);
			primeIndexMaxPermutation.set(numBitsForPrimeCount); // keep 'shifting' max bit left

			// no reason to perform work when no indexes
			// selected so start with 1 index selected.
			primeIndexPermutation.clear();
			primeIndexPermutation.set(0);

			final var sumCeiling = curPrime.stream().map(this::calcSumCeiling).reduce(BigInteger.ZERO, BigInteger::add);

			var doLoop = !(primeIndexPermutation.equals(primeIndexMaxPermutation));
			while (doLoop)
			{
				final var permutationSum = primeIndexPermutation
						.stream()
						.mapToObj(this::getPrime)
						.filter(Optional::isPresent)
						.map(Optional::get)
						.reduce(curPrime.orElse(BigInteger.ZERO), BigInteger::add);

				// exceed known prime then iteration is done - limit useless work
				doLoop = permutationSum.compareTo(sumCeiling) <= 0;
				if (doLoop)
				{
					if (curPrime.isPresent() && viablePrime(permutationSum, curPrime.get()))
					{
						final var sumBaseIdxs = primeIndexPermutation.get(0, numBitsForPrimeCount);
						sumBaseIdxs.set(nextIdx.get());

						addPrimeRef(permutationSum, sumBaseIdxs.stream().boxed().collect(Collectors.toCollection(TreeSet::new)));
						doLoop = false;
					}
					else
						incrementPermutation(primeIndexPermutation);
				}
			}


			// display some progress info
			if (nextIdx.get() % 1000 == 0)
			{
				primesProcessed1k++;
				if (primesProcessed1k % 100 == 0)
				{
					System.out.println(String.format("%n %d00k primes", (int)(primesProcessed1k / 100)));
				}
				else
				{
					System.out.print("K");
				}
			}
			allPrimesProcessed = nextIdx.get() >= targetPrimeCount;

		}
		while (!allPrimesProcessed);
	}

	@Override
	public void store(BaseTypes ... baseTypes)
	{

		Map<Integer, BigInteger> primeCache = cacheMgr.getCache("primes");
		primeCache.putAll(primes);

		Map<Integer, PrimeRefIntfc> primeRefCache = cacheMgr.getCache("primerefs");
		primeRefCache.putAll(primeRefs);

		if (log.isLoggable(Level.INFO))
			log.info(String.format("Stored [%d] primes, [%d] PrimeRefs", primeCache.size(), primeRefCache.size()));
		//if (baseTypes != null)
		//	cacheMgr.getCache(BaseTypes.DEFAULT.name()).putAll(primeRefs.get(BaseTypes.DEFAULT));
	}

	@Override
	public void load(BaseTypes ... baseTypes)
	{
		if (doInit.getPlain())
		{
			return;
		}
		Map<Integer, BigInteger>  prCache = cacheMgr.getCache("primes");
		if (!prCache.isEmpty())
		{
			primes.putAll(prCache);

			if (log.isLoggable(Level.INFO))
				log.info(String.format("cached prime entries loaded: %d ", primes.size()));
		}
		else
		{
			if (log.isLoggable(Level.INFO))
				log.info("prime cache empty");
		}

		Map<Integer, PrimeRef>  prRefCache = cacheMgr.getCache("primerefs");
		if (!prRefCache.isEmpty())
		{
			//prRefCache.forEach((k,v) -> {v.init(null, baseSetPrimeSrc); primeRefs.put(k, v); });
			primeRefs.putAll(prRefCache);

			if (log.isLoggable(Level.INFO))
				log.info(String.format("cached prime ref entries loaded: %d ", primeRefs.size()));
		}
		else
		{
			if (log.isLoggable(Level.INFO))
				log.info("prime refs cache empty");
		}
	}

	@Override
	public void setActiveBaseId(@NonNull BaseTypes activeBaseId)
	{
		this.activeBaseId = activeBaseId;
	}

	@Override
	public Iterator<PrimeRefIntfc> getPrimeRefIter()
	{
		return primeRefs.values().iterator();
	}

	@Override
	public Stream<PrimeRefIntfc> getPrimeRefStream(boolean preferParallel)
	{
		return preferParallel ? primeRefs.values().parallelStream() : primeRefs.values().stream();
	}

	@Override
	public Optional<BigInteger> getPrime(@Min(0) int primeIdx)
	{
		if (!primes.containsKey(primeIdx))
			return Optional.empty();

		return Optional.of(primes.get(primeIdx));
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRef(@Min(0) int primeIdx)
	{
		if (primeIdx < 0 || primeIdx >= primeRefs.size())
			return Optional.empty();

		return   Optional.of(primeRefs.get(primeIdx));
	}

	private BigInteger calcSumCeiling(@NonNull @Min(3) BigInteger primeSum)
	{
		return primeSum.shiftLeft(1).subtract(BigInteger.ONE);
	}

	private void incrementPermutation(@NonNull BitSet primePermutation)
	{
		var bit = 0;
		// Generate next permutation of the bit indexes
		do	// performs add and carry if needed
		{
			primePermutation.flip(bit);

			if (primePermutation.get(bit)) // true means no carry
				break;

			bit++;
		} while(true);
	}

	/**
	 * Add new Prime to shared set of all primes
	 *
	 * @param aPrime
	 */
	private void addPrimeRef(
			@NonNull @Min(1) BigInteger newPrime,
			@NonNull Set<Integer> base
			)
	{
		var idx = nextIdx.incrementAndGet();

		primes.put(idx, newPrime);
		PrimeRefIntfc ret = primeRefCtor.apply(idx, base);
		primeRefs.put(idx, ret);
	}

	/**
	 *
	 * Weed out sums which cannot represent the next Prime.
	 *
	 * @param sumOfPrimeSet
	 * @return true for sum that is viable Prime; false otherwise
	 */
	private boolean viablePrime(@NonNull @Min(1) BigInteger primeSum, @Min(1) BigInteger lastMaxPrime)
	{
		return
			// Part of "non-cheating" Prime checks.
			// only want sets summing to greater than the current
			// max Prime.
				primeSum.compareTo(lastMaxPrime) > 0
			&&
			// Part of "non-cheating" Prime checks.
			// Not a Prime if is even.
				primeSum.testBit(0)
			&&
				// This is bootstrap logic.
				// If possible, this block should be removed and allow the remaining blocks to determine next Prime.
				primeSum.isProbablePrime(confidenceLevel);
	}
}
