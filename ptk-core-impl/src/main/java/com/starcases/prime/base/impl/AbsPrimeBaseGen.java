package com.starcases.prime.base.impl;

import com.starcases.prime.base.api.BaseGenFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.Permutation;
import lombok.AccessLevel;
import lombok.Getter;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;

import java.util.BitSet;
import java.util.Optional;
import java.util.OptionalLong;

/**
 *
 * Abstract class for common Prime base functionality.
 *
 */
public abstract class AbsPrimeBaseGen implements BaseGenFactoryIntfc
{
	private final int MAX_PRIME_BITS = 64;

	protected MutableList<Long[]> subsetColl = MutableListFactoryImpl.INSTANCE.empty();

	/**
	 * Access to lookup of prime/primerefs and the init of base information.
	 */
	@Getter(AccessLevel.PROTECTED)
	protected PrimeSourceIntfc primeSrc;

	/**
	 * Indicator for whether a sum is over/under/equal desired value or must-undo change
	 */
	protected enum State { OVER, UNDER, EQUAL, REVERT }

	/**
	 * Flag indicating whether base construction can use multiple CPU cores
	 */
	@Getter(AccessLevel.PROTECTED)
	protected boolean preferParallel;

	protected final int minIdx;
	protected final int maxIdx;

	/**
	 * Constructor for secondary bases.
	 * @param primeSrc
	 */
	protected AbsPrimeBaseGen(final int minIdx, final int maxIdx)
	{
		this.minIdx = minIdx;
		this.maxIdx = maxIdx;
	}

	/**
	 * fluent style method for setting flag for whether base construction can use multiple CPU cores.
	 * @param preferParallel
	 * @return
	 */
	@Override
	public BaseGenFactoryIntfc doPreferParallel(final boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	@Override
	public BaseGenFactoryIntfc assignPrimeSrc(final PrimeSourceIntfc primeSrc)
	{
		this.primeSrc = primeSrc;
		return this;
	}

	protected MutableLongList findPrefixes2(final PrimeRefIntfc curPrime)
	{
		final MutableLongList bases = LongLists.mutable.of();

		if (curPrime.getPrime() - 2 <= 0)
		{
			bases.add(curPrime.getPrime());
		}
		else
		{
			long topIndex = curPrime.getPrimeRefIdx();
			long remain = -1;
			Optional<PrimeRefIntfc> topPrime = Optional.of(curPrime);

			while (topIndex > 0 && topPrime.isPresent() && remain != 0)
			{
				bases.clear();
				remain = curPrime.getPrime();
				long prevIndex = topIndex--;
				boolean doContinue;

				do
				{
					final long prevPrime = primeSrc.getPrimeForIdx(--prevIndex).getAsLong();

					final long tmpRemain = remain - prevPrime;
					if (tmpRemain >= 0)
					{
						remain = tmpRemain;
						bases.add(prevPrime);
					}
					doContinue = prevIndex > 0 && remain > 0;
				}
				while(doContinue);

				topPrime = topPrime.get().getPrevPrimeRef();
			}
		}

		return bases;
	}

	/**
	 * Should produce the longest prefix due to starting with lowest values first.
	 *
	 * @param tgtPrime
	 * @return
	 */
	protected MutableLongList findPrefixesLowFirst(final PrimeRefIntfc tgtPrime)
	{
		final MutableLongList bases = LongLists.mutable.of();

		if (tgtPrime.getPrime() - 2 <= 0)
		{
			bases.add(tgtPrime.getPrime());
		}
		else
		{
			final Optional<PrimeRefIntfc> topPrime = primeSrc.getPrimeRefForIdx(tgtPrime.getPrimeRefIdx()-1);
			bases.add(topPrime.get().getPrime());

			final var remain = tgtPrime.getPrime() - topPrime.get().getPrime();

			// n-bit permutation starting with value of 0
			final var primeIndexPermutation = new BitSet();
			boolean done = false;
			do
			{
				// Check this against the target Prime.
				final var permutationSum = primeIndexPermutation
						.stream()
						.mapToObj(primeSrc::getPrimeForIdx)
						.filter(OptionalLong::isPresent)
						.map(OptionalLong::getAsLong)
						.reduce(0L, Long::sum);

				if (permutationSum == remain)
				{
					primeIndexPermutation
					.stream()
					.mapToObj(primeSrc::getPrimeForIdx)
					.filter(OptionalLong::isPresent)
					.map(OptionalLong::getAsLong)
					.forEach(bases::add);
					done = true;
				}
				else
				{
					if (primeIndexPermutation.length() <= MAX_PRIME_BITS)
					{
						Permutation.incrementPermutation(primeIndexPermutation);
					}
					else // ensure we don't increment forever
					{
						System.out.println(String
								.format("findPrefixesLowFirst [incomplete bases] - tgtIdx: %d tgtPrime: %d perm-sum: %d, remain: %d, permutation: %s",
										tgtPrime.getPrimeRefIdx(),
										tgtPrime.getPrime(),
										permutationSum,
										remain,
										primeIndexPermutation));

						done = true;
					}
				}
			}
			while (!done);
		}
		return bases;
	}
}
