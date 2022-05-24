package com.starcases.prime.intfc;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Optional;

/**
 *
 * Main interface for working with the primes and accessing the info on
 * bases.
 *
 * Generally, the primes are not stored in the classes implementing this
 *  interface.
 *
 * The PrimeSourceIntfc implementations generally store the primes and other
 *  directly related data - this facilitated the use of integer indexes and
 *   data types which support near O(1) lookups by index.  The index should
 *   be managed by each class implementing this PrimeRefIntfc interface.
 *
 */
@SuppressWarnings("PMD.CommentSize")
public interface PrimeRefIntfc extends Serializable
{
	/**
	 * Prime ref compatator
	 */
	Comparator<PrimeRefIntfc> PREF_COMPARATOR = (PrimeRefIntfc o1, PrimeRefIntfc o2) -> o1.getPrime().compareTo(o2.getPrime());

	/**
	 * Get distance to next prime from current prime.
	 * @return
	 */
	Optional<BigInteger> getDistToNextPrime();

	/**
	 * Get distance to previous prime from current prime.
	 * @return
	 */
	Optional<BigInteger> getDistToPrevPrime();

	/**
	 * Get the next prime based on numerical ordering
	 * @return
	 */
	Optional<PrimeRefIntfc> getNextPrimeRef();

	/**
	 * get the previous prime based on numerical ordering.
	 * @return
	 */
	Optional<PrimeRefIntfc> getPrevPrimeRef();

	/**
	 * Get big integer representing the current prime ref.
	 * @return
	 */
	BigInteger getPrime();

	/**
	 * Get container for prime base data.
	 * @return
	 */
	PrimeBaseIntfc getPrimeBaseData();

	/**
	 *
	 * @return int representing representing index in
	 * overall list of primes.
	 */
	long getPrimeRefIdx();
}
