package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Optional;

import lombok.NonNull;

/**
 *
 * Main interface for working with the primes and accessing the info on bases.
 *
 */
public interface PrimeRefIntfc
{
	static final Comparator<PrimeRefIntfc> primeRefComparator = (PrimeRefIntfc o1, PrimeRefIntfc o2) -> o1.getPrime().compareTo(o2.getPrime());

	// prime reference related
	BigInteger getPrime();
	Optional<PrimeRefIntfc> getPrimeRefWithinOffset(@NonNull BigInteger targetOffset);

	Optional<PrimeRefIntfc> getNextPrimeRef();
	Optional<PrimeRefIntfc> getPrevPrimeRef();

	Optional<BigInteger> getDistToNextPrime();
	Optional<BigInteger> getDistToPrevPrime();

	//
	// Index related info
	//

	/**
	 *
	 * @return int representing representing index in
	 * overall list of primes.
	 */
	int getPrimeRefIdx();


	PrimeBaseIntfc getPrimeBaseData();
}
