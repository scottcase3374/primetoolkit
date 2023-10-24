package com.starcases.prime.core.api;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;
import java.util.OptionalLong;

import com.starcases.prime.base.api.PrimeBaseIntfc;

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
public interface PrimeRefIntfc extends Serializable
{
	/**
	 * Prime ref compatator
	 */
	Comparator<PrimeRefIntfc> PREF_COMPARATOR = (PrimeRefIntfc o1, PrimeRefIntfc o2) -> (int)(o1.getPrime() - o2.getPrime());

	/**
	 * Get distance to next prime from current prime.
	 * @return
	 */
	OptionalLong getDistToNextPrime();

	/**
	 * Get distance to previous prime from current prime.
	 * @return
	 */
	OptionalLong getDistToPrevPrime();

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
	long getPrime();

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

	boolean hasNext();
	boolean hasPrev();
}
