package com.starcases.prime.base.triples.impl;

import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.impl.PrimeBaseGenerator;
import com.starcases.prime.core.api.PrimeRefIntfc;

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
class BaseReduceTriple extends PrimeBaseGenerator
{
	/**
	 * Constructor
	 *
	 * @param primeSrc
	 */
	public BaseReduceTriple(@NonNull final BaseTypesIntfc baseType)
	{
		super(baseType);
	}

	/**
	 * Generate base for specified prime
	 */
	@Override
	public void genBasesForPrimeRef(final PrimeRefIntfc curPrime)
	{
		new AllTriples(primeSrc, this.preferParallel).process(curPrime);
	}
}
