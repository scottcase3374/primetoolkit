package com.starcases.prime.base.tripleng.impl;

import java.util.Optional;

import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.impl.list.immutable.primitive.ImmutableLongListFactoryImpl;

import com.starcases.prime.base.impl.AbsPrimeBaseGen;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;

import lombok.NonNull;

/*
 *  Given a Prime, find EVERY set of 3 pre-existing primes
 *   that sum to the Prime.
 *
 *
 *  A few examples of a single triple (or 2) per Prime.
 *     P     B
 *	   R     A
 *  I  I     S
 *  D  M     E
 *  X  E     S
 *
 *  0  1      x
 *  1  2      x
 *  2  3      x
 *  3  5      x
 *  4  7	  x
 *  5 11 -> 1,3,7
 *  6 13 -> 1,5,7
 *  7 17 -> 1,5,11
 *  8 19 -> 1,7,11
 *  9 23 -> 5,7,11 *  [* note contiguous primes];   3,7,13
 * 10 29 -> 1,11,17; 5,11,13
 * 11 31 -> 7,11,13 *
 * 12 37 -> 7,13,17
 * 13 41 -> 11,13,17 *;  5,13,23
 * 14 43 -> 7,17,19
 * 15 47 -> 7,17,23; 5,19,23
 * 16 53 -> 11,19,23
 * 17 59 -> 17,19,23 *
 * 18 61 -> 1,29,31
 * 19 67 -> 7,29,31
 * 20 71 -> 11,29,31
 * 21 73 -> 13,29,31
 * 22 79 -> 19,29,31
 * 23 83 -> 23,29,31 *
 * 24 89 -> 19,29,41
 * 25 97 -> 13,41,43; 19,37,41
 * 26 101-> 23,37,41
 * 27 103-> 19,41,43
 * 28 107->
 * 29 109-> 31,37,41 *
 */

/**
 * Produce "triples" for each prime.
 */
class BaseReduceTripleng extends AbsPrimeBaseGen
{
	/**
	 * Constructor
	 *
	 * @param primeSrc
	 */
	public BaseReduceTripleng(@NonNull final BaseTypesIntfc baseType)
	{
		super(baseType);
	}

	private boolean adjustIndexes(@NonNull final long [] lowIdx, @NonNull final long [] mediumIdx, @NonNull final long [] highIdx)
	{
		boolean retVal = true;
		lowIdx[0]++;
		if (lowIdx[0] == mediumIdx[0])
		{
			mediumIdx[0]++;
			lowIdx[0] = 0;
		}
		if (mediumIdx[0] == highIdx[0])
		{
			highIdx[0]--;
			mediumIdx[0] = 1;
			lowIdx[0] = 0;
		}

		if (mediumIdx[0] == highIdx[0])
		{
			retVal = false;
		}
		return retVal;
	}
	/**
	 * Generate base for specified prime
	 */
	@Override
	public void genBasesForPrimeRef(@NonNull final PrimeRefIntfc curPrime)
	{
		final long [] lowIdx = {-1};
		final long [] mediumIdx = {0};
		final long [] highIdx = {curPrime.getPrimeRefIdx()};

		while(adjustIndexes(lowIdx, mediumIdx, highIdx))
		{
			final long lowPrime = primeSrc.getPrimeForIdx(lowIdx[0]).getAsLong();
			final long mediumPrime = primeSrc.getPrimeForIdx(mediumIdx[0]).getAsLong();
			final long highPrime = curPrime.getPrime() - mediumPrime - lowPrime;

			if (highPrime <= mediumPrime)
			{
				break;
			}

			Optional<PrimeRefIntfc> highRef = primeSrc.getPrimeRefForPrime(highPrime);
			if (highRef.isPresent())
			{
				highIdx[0] = highRef.get().getPrimeRefIdx();
				addPrimeBases(curPrime, ImmutableLongListFactoryImpl.INSTANCE.of(primeSrc.getPrimeForIdx(lowIdx[0]).getAsLong(),
															  primeSrc.getPrimeForIdx(mediumIdx[0]).getAsLong(),
															  primeSrc.getPrimeForIdx(highIdx[0]).getAsLong()
															));
				lowIdx[0] = -1;
				mediumIdx[0] = 0;
			}
		}
	}

	private void addPrimeBases(@NonNull final PrimeRefIntfc prime, @NonNull final ImmutableLongList triple)
	{
//		cache.ifPresent(c -> c.put(prime.getPrime(), triple.toArray()));
		prime.getPrimeBaseData().addPrimeBases(prime.getPrimeRefIdx(), triple, TriplengBaseType.TRIPLENG);
	}
}
