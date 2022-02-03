package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Optional;

import lombok.NonNull;

/**
 *
 * Main interface for working with the primes and accessing the info on bases.
 *
 * Generally, the primes are not stored in the classes implementing this interface.
 *
 * The PrimeSourceIntfc implementations generally store the primes and other directly
 * related data - this facilitated the use of integer indexes and data types
 * which support near O(1) lookups by index.  The index should be managed by each
 * class implementing this PrimeRefIntfc interface.
 *
 */
public interface PrimeRefIntfc
{
	static final Comparator<PrimeRefIntfc> primeRefComparator = (PrimeRefIntfc o1, PrimeRefIntfc o2) -> o1.getPrime().compareTo(o2.getPrime());

	//
	// prefixPrime reference related
	//

	BigInteger getPrime();
	Optional<PrimeRefIntfc> getPrimeRefWithinOffset(@NonNull BigInteger targetOffset);

	Optional<PrimeRefIntfc> getNextPrimeRef();
	Optional<PrimeRefIntfc> getPrevPrimeRef();

	Optional<BigInteger> getDistToNextPrime();
	Optional<BigInteger> getDistToPrevPrime();

	//
	// Base related info
	//
	PrimeBaseIntfc getPrimeBaseData();

	//
	// Index related info
	//

	/**
	 *
	 * @return int representing representing index in
	 * overall list of primes.
	 */
	int getPrimeRefIdx();
}
