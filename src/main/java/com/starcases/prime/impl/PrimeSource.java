package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.concurrent.atomic.AtomicBoolean;

import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.base.primetree.PrimeTree;
import com.starcases.prime.intfc.CollectionTrackerIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Min;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectLongHashMap;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;

/**
 * Provides data structure holding the core data and objects that provide
 * access to the data from the
 * "public interface for primes". Also some utility support to support alternative algs for
 * finding primes, working with bases of the primes, and general information/access.
 *
 */
@SuppressWarnings({"PMD.LongVariable", "PMD.CommentSize", "PMD.AvoidDuplicateLiterals"})
public class PrimeSource extends AbstractPrimeBaseGenerator implements PrimeSourceIntfc
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(PrimeSource.class.getName());

	private static final long serialVersionUID = 1L;

	/**
	 * atomic flag preventing duplicate init
	 */
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final AtomicBoolean doInit = new AtomicBoolean(false);

	/**
	 * confidence level for check of prime
	 */
	@Getter(AccessLevel.PRIVATE)
	@Min(1)
	private final int confidenceLevel;

	/**
	 * atomic long for next prime index.
	 */
	@Getter(AccessLevel.PRIVATE)
	@Min(-1)
	private final AtomicLong nextIdx = new AtomicLong(-1);

	/**
	 * number of base primes to generate
	 */
	@Getter(AccessLevel.PRIVATE)
	@Min(1)
	private final long targetPrimeCount;

	/**
	 * flag indicating whether to output progress
	 * during initial base creation
	 */
	@Setter
	@Getter(AccessLevel.PRIVATE)
	private boolean displayProgress;

	/**
	 * flag indicating whether to output prefix/tree
	 * metrics from initial prime creation
	 */
	@Setter
	@Getter()
	private boolean displayPrimeTreeMetrics;

	/** can be overridden by passing
	* an appropriate "new" function ptr into the PrimeSource
	* constructor  -> param primeRefCtor
	*/
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final BiFunction<Long, List<Set<BigInteger>>, PrimeRefIntfc> primeRefRawCtor;

	/**
	 * Track collections as part of prefix/tree tracking
	 */
	@Getter(AccessLevel.PRIVATE)
	private final CollectionTrackerIntfc collTrack;

	/**
	 * container for the tree info
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private PrimeTree primeTree;

	/**
	 *  map BigInteger (prime) to long index
	 */
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final MutableObjectLongMap<BigInteger> primeToIdx;

	/** map long idx to PrimeMapEntry,  PrimeMapEntry is primeRef and BigInt
	* long idx provides order to primerefs and therefore indirectly to the primes
	*/
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final MutableLongObjectMap<PrimeMapEntry> idxToPrimeMap;

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
			@NonNull final List<Consumer<PrimeSourceIntfc>> consumersSetPrimeSrc,
			@Min(1) final int confidenceLevel,
			@NonNull final BiFunction<Long, List<Set<BigInteger>>, PrimeRefIntfc> primeRefRawCtor,
			final CollectionTrackerIntfc collTrack
			)
	{
		super();
		super.primeSrc = this;

		this.collTrack = collTrack;

		final int capacity = (int)(maxCount*1.25);
		idxToPrimeMap = new LongObjectHashMap<>(capacity);
		primeToIdx = new ObjectLongHashMap<>(capacity);

		targetPrimeCount = maxCount;

		this.primeRefRawCtor = primeRefRawCtor;
		consumersSetPrimeSrc.forEach(c -> c.accept(this));

		// Prime, Prefix, suffix
		addPrimeRef(BigInteger.ONE, List.of(TreeSortedSet.newSetWith(BigInteger.ONE), TreeSortedSet.newSetWith(BigInteger.ZERO)));
		addPrimeRef(BigInteger.TWO, List.of(TreeSortedSet.newSetWith(BigInteger.TWO), TreeSortedSet.newSetWith(BigInteger.ZERO)));

		this.confidenceLevel = confidenceLevel;
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

	/**
	 * This is the "prime" logic so to speak..  the "innovative" side of this isn't
	 * the initial identification of primes but the data that is created related
	 * to each prime - the "bases".  The question to ask is - if given some
	 * items representing each prime; is there an identifiable pattern over
	 * any range of primes that provides insights into faster identification
	 * of much larger primes? Can something new be learned with regard to factors
	 * of numbers created from large primes?
	 */
	@Override
	protected void genBasesImpl()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("PrimeSource::genBasesImpl()");
		}

		primeTree = new PrimeTree(this, collTrack);

		long curTmpIdx;
		do
		{
			final var curPrimeIdx = nextIdx.get();
			curTmpIdx = curPrimeIdx;
			final var curPrime = getPrime(curPrimeIdx);

			if (!genByListPermutation(curPrime))
			{
				genByPrimePermutation(curPrime);
			}

			if (displayProgress)
			{
				dispProgress(curPrimeIdx);
			}
		}
		while (curTmpIdx < targetPrimeCount);
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	private void genByPrimePermutation(final Optional<BigInteger> curPrime)
	{
		// Represents a X-bit search space of indexes for primes to add for next Prime.
		final var numBitsForPrimeCount = primeToIdx.size()-1;
		final var primeIndexMaxPermutation = new BitSet();
		primeIndexMaxPermutation.set(numBitsForPrimeCount); // keep 'shifting' max bit left

		// no reason to perform work when no indexes
		// selected so start with 1 index selected.
		final var primeIndexPermutation = new BitSet();
		primeIndexPermutation.set(0);

		final var sumCeiling = curPrime.stream().map(this::calcSumCeiling).reduce(BigInteger.ZERO, BigInteger::add);

		var doLoop = !(primeIndexPermutation.equals(primeIndexMaxPermutation));
		while (doLoop)
		{
			final BigInteger permutationSum = primeIndexPermutation
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
					final var prefix = primeTree.iterator();
					primeIndexPermutation.stream().forEach(i ->
						{
							final var prefPrime = getPrime(i).get();

							prefix.add(prefPrime);
						});

					final var tmpS = prefix.toSet();
					final var prefixSet = collTrack.track(tmpS).coll();

					addPrimeRef(permutationSum, List.of(prefixSet, Set.of(curPrime.get())));
					doLoop = false;
				}
				else
				{
					incrementPermutation(primeIndexPermutation);
				}
			}
		}
	}

	private boolean genByListPermutation(final Optional<BigInteger> curPrime)
	{
		boolean [] found = {false};
		curPrime.ifPresent(cur ->
						{
							final var tmpCur = cur;
							final var pdata = primeTree.select(primeLong -> { final var prime = tmpCur.add(BigInteger.valueOf(primeLong));
																return prime.isProbablePrime(100); });
							pdata.ifPresent( pd ->
										{
											found[0] = true;
											addPrimeRef(tmpCur.add(BigInteger.valueOf(pd.prime())), List.of(pd.coll(), Set.of(tmpCur)));
										}
									);
							});

		return found[0];
	}

	private void dispProgress(final long curIndex)
	{
		final String [] units = { "%n[%s x 1024k] ", "%n [%s x 128k] ", "%n [%s x 16k] ", "K"};
		final long [] mask = {(1 << 20) -1, (1 << 17) -1 , (1 << 14) -1, (1 << 10) -1};

		// display some progress info
		final var primesProcessed = curIndex;
		int unitIdx = -1;
		while ((primesProcessed & mask[++unitIdx]) != 0 && unitIdx <  mask.length-1)
		{
			 // loop expression does the work
		}

		final var num = (int)(primesProcessed / mask[unitIdx]);
		if (unitIdx != units.length-1 || num <= 2)
		{
			PrimeToolKit.output(String.format(units[unitIdx], num));
		}
	}


	@Override
	public void store(final BaseTypes ... baseTypes)
	{
		// not used as of now
	}

	@Override
	public void load(final BaseTypes ... baseTypes)
	{
		// not used as of now
	}

	@Override
	public java.util.Iterator<PrimeRefIntfc> getPrimeRefIter()
	{
		return new PrimeMapIterator(idxToPrimeMap.iterator());
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public Stream<PrimeRefIntfc> getPrimeRefStream(final boolean preferParallel)
	{
		return (preferParallel ? idxToPrimeMap.values().parallelStream() : idxToPrimeMap.values().stream()).map(PrimeMapEntry::getPrimeRef);
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public Stream<PrimeRefIntfc> getPrimeRefStream(final long skipCount, final boolean preferParallel)
	{
		return (preferParallel ? idxToPrimeMap.values().stream().skip(skipCount).parallel() : idxToPrimeMap.values().stream().skip(skipCount)).map(PrimeMapEntry::getPrimeRef);
	}

	@Override
	public Optional<BigInteger> getPrime(@Min(0) final long primeIdx)
	{
		return Optional.ofNullable(getIdxToPrime(primeIdx));
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRef(@Min(0) final BigInteger prime)
	{
		return Optional.ofNullable(getPrimeToPrimeRef(prime));
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRef(@Min(0) final long primeIdx)
	{
		final var primeRef = getIdxToPrimeRef(primeIdx);
		return Optional.of(primeRef);
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	private BigInteger calcSumCeiling(@NonNull @Min(3) final BigInteger primeSum)
	{
		return primeSum.shiftLeft(1).subtract(BigInteger.ONE);
	}

	private void incrementPermutation(@NonNull final BitSet primePermutation)
	{
		var bit = 0;
		// Generate next permutation of the bit indexes
		do	// performs add and carry if needed
		{
			primePermutation.flip(bit);

			if (primePermutation.get(bit)) // true means no carry
			{
				break;
			}

			bit++;
		} while(true);
	}

	/**
	 * Add new Prime to shared set of all primes
	 *
	 * @param aPrime
	 */
	private void addPrimeRef(
			@NonNull @Min(1) final BigInteger newPrime,
			@NonNull final List<Set<BigInteger>> baseSets
			)
	{
		final var idx = nextIdx.incrementAndGet();
		final PrimeRefIntfc ret = primeRefRawCtor.apply(idx, baseSets);
		updateMaps(idx, newPrime, ret);
	}

	private void updateMaps(final long idx, final BigInteger newPrime, final PrimeRefIntfc ref)
	{
		this.idxToPrimeMap.put(idx, new PrimeMapEntry(newPrime, ref));
		this.primeToIdx.put(newPrime, idx);
	}

	/**
	 *
	 * Weed out sums which cannot represent the next Prime.
	 *
	 * @param sumOfPrimeSet
	 * @return true for sum that is viable Prime; false otherwise
	 */
	private boolean viablePrime(@NonNull @Min(1) final BigInteger primeSum, @Min(1) final BigInteger lastMaxPrime)
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
				// If possible, this block should be removed and allow the
				// remaining blocks to determine next Prime.
				primeSum.isProbablePrime(confidenceLevel);
	}

	private long getPrimeToIdx(final BigInteger prime)
	{
		return primeToIdx.get(prime);
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	private PrimeRefIntfc getIdxToPrimeRef(final long idx)
	{
		return idxToPrimeMap.get(idx).getPrimeRef();
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	private BigInteger getIdxToPrime(final long idx)
	{
		final var ret = idxToPrimeMap.get(idx);
		return ret != null ? ret.getPrime() : null;
	}

	private PrimeRefIntfc getPrimeToPrimeRef(final BigInteger prime)
	{
		return getIdxToPrimeRef(getPrimeToIdx(prime));
	}
}
