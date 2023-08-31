package com.starcases.prime.core.impl;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.common.api.OutputOper;
import com.starcases.prime.core.api.PrimeGenIntfc;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.api.ProgressIntfc;
import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.datamgmt.api.PData;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.metrics.api.MetricIntfc;
import com.starcases.prime.metrics.api.MetricProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class PrimeGenImpl implements PrimeGenIntfc
{
	/**
	 * default logger
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final Logger LOG = Logger.getLogger(PrimeGenImpl.class.getName());

	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();

	/**
	 * atomic long for next prime index.
	 */
	@Getter(AccessLevel.PRIVATE)
	private final AtomicLong nextIdx = new AtomicLong(1);

	//
	// Metrics / health / progress
	//
	@Getter
	private final MetricProviderIntfc metricProvider;

	private PrimeSourceIntfc primeSrc;

	/**
	 * dest of output progress tracking
	 * during initial base creation
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private ProgressIntfc progress;


	private CollectionTrackerIntfc collTracker;

	public PrimeGenImpl(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final CollectionTrackerIntfc collTracker, @NonNull final MetricProviderIntfc metricProvider)
	{
		this.primeSrc = primeSrc;
		this.collTracker = collTracker;
		this.metricProvider = metricProvider;
	}

	private long calcSumCeiling(@Min(3) final long primeSum)
	{
		return (primeSum << 1) - 1L;
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
	public void generate(final long startIdx, final long endIdx, final boolean saveNew)
	{
		long nextPrimeTmpIdx = Long.MAX_VALUE;
		do
		{
			try(MetricIntfc metric = metricProvider.longTimer(OutputOper.CREATE_PRIMES, "PrimeSource", "genPrimes"))
			{
				final int nextPrimeIdx = (int) nextIdx.incrementAndGet();
				final int lastPrimeIdx = nextPrimeIdx -1;
				final OptionalLong lastPrime = primeSrc.getPrimeForIdx(lastPrimeIdx);

				genByListPermutation(nextPrimeIdx, lastPrime, saveNew)
											.orElse(genByPrimePermutation(nextPrimeIdx, lastPrime, saveNew)
											.orElseThrow( () -> new IllegalStateException("No prime found")));


				if (null != progress)
				{
					progress.dispProgress(nextPrimeIdx);
				}

				nextPrimeTmpIdx = nextPrimeIdx;
			}
			catch(final Exception e) // due to autoclosable interface.
			{
				// rethrows
				statusHandler.handleError(() -> "Error handling prime generation.", Level.SEVERE, e, true, true);
			}
		}
		while (nextPrimeTmpIdx < endIdx);
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
	private Optional<PrimeRefIntfc> genByListPermutation(@Min(0) final long nextPrimeIdx, @Min(1) final OptionalLong optCurPrime, final boolean saveNew)
	{
		final PrimeRefFactoryIntfc [] retPrRef = {null};

		optCurPrime.ifPresent(curPrime ->
						{
							// NOTE: Selection process must pick lowest item
							final Optional<PData> pdata = collTracker.select(
									primeCollSum ->
									{
										final long possibleNextPrime = curPrime + primeCollSum;
										return BigInteger.valueOf(possibleNextPrime).isProbablePrime(100);
									});

							pdata.ifPresent( pd ->
										{
											final var nextPrime = curPrime + pd.prime();

											if (saveNew)
											{
												retPrRef[0] = primeSrc.addPrimeRef(nextPrimeIdx, nextPrime);
											}
										}
									);
							});

		if (LOG.isLoggable(Level.FINE))
		{
			LOG.fine("gen by list permutation - foundPrime idx:" + nextPrimeIdx + " prime:" + optCurPrime.getAsLong());
		}
		return Optional.ofNullable(retPrRef[0]);
	}

	/**
	 * This is
	 * fundamentally calculating log-base-2 (+1) for the value. Slightly
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
	private Optional<PrimeRefIntfc> genByPrimePermutation(@Min(0) final long idx, @Min(1) final OptionalLong optCurPrime, final boolean saveNew)
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

		PrimeRefIntfc foundPrime = null;
		var doLoop = !primeIndexPermutation.equals(primeIndexMaxPermutation);
		while (doLoop)
		{
			final long permutationSum = primeIndexPermutation
					.stream()
					.mapToObj(i -> primeSrc.getPrimeForIdx(i))
					.filter(OptionalLong::isPresent)
					.map(OptionalLong::getAsLong)
					.reduce(optCurPrime.orElse(0), (a, b) -> a+b);

			// FIXME There is a problem with a few primes getting missed related to the comment
			// that follows this related to "limits useless work". The permutation
			// process based on a binary bitmask of primes to sum doesn't produce a stable sum
			// by simply incrementing the binary bitmask value by 1 across all values.

			// If exceed known prime then iteration is done - limits useless work
			doLoop = permutationSum - sumCeiling <= 0;
			if (doLoop)
			{
				final var optCurPrimeVal = optCurPrime.getAsLong();
				if (optCurPrime.isPresent() && viablePrime(permutationSum, optCurPrimeVal))
				{
					if (saveNew)
					{
						final var prefixTree = collTracker.iterator();
						primeIndexPermutation.stream().forEach(prefixIdx -> primeSrc.getPrimeForIdx(prefixIdx).ifPresent(prefixTree::add) );

						final var primeTreeColl = prefixTree.toCollection();
						collTracker.track(primeTreeColl);

						foundPrime = primeSrc.addPrimeRef(idx, permutationSum);
					}
					doLoop = false;
				}
				else
				{
					incrementPermutation(primeIndexPermutation);
				}
			}
		}

		if (LOG.isLoggable(Level.FINE))
		{
			LOG.fine("gen by prime permutation - foundPrime idx:" + idx + " prime:" + optCurPrime.getAsLong());
		}
		return Optional.ofNullable(foundPrime);
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
			;
	}

}
