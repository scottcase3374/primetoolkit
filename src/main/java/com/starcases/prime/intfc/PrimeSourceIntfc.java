package com.starcases.prime.intfc;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import javax.validation.constraints.Min;

import com.starcases.prime.base.BaseTypes;

/**
 *
 * Main interface for needs related to Prime / base
 * generation and alternative algs.
 *
 * Implementations of this interface generally contain the
 * primes data and directly data
 * directly related to the primes.  This allows near direct
 * access to the individual primes
 * and data for navigating amongst the primes.
 *
 */
@SuppressWarnings("PMD.CommentSize")
public interface PrimeSourceIntfc extends Serializable
{
	/**
	 * Set flag indicating whether to output progress of initial base creation.
	 * @param doDisplay
	 */
	void setDisplayProgress(boolean doDisplay);

	/**
	 * set flag indicating whether to output metrics regarding the
	 * prime trees generated for initial base.
	 * @param doDisplay
	 */
	void setDisplayPrimeTreeMetrics(boolean doDisplay);

	/**
	 * Init base info.
	 */
	void init();

	/**
	 * Store base info in cache.
	 * Currently not used.
	 *
	 * @param baseTypes
	 */
	void store(BaseTypes ... baseTypes);

	/**
	 * Load base info from cache.
	 * Currently not used.
	 * @param baseTypes
	 */
	void load(BaseTypes ... baseTypes);

	/**
	 * Iterator to PrimeRefIntfc instances.
	 *
	 * @return
	 */
	Iterator<PrimeRefIntfc> getPrimeRefIter();

	/**
	 * Get a stream of prime refs and indicate whether parallel stream ops
	 * are allowed.
	 *
	 * @param preferParallel
	 * @return
	 */
	Stream<PrimeRefIntfc> getPrimeRefStream(boolean preferParallel);

	/**
	 * Get a stream of prime refs  after skipping
	 * an initial count; indicate whether parallel stream ops
	 * are allowed.
	 *
	 * @param skipCount
	 * @param preferParallel
	 * @return
	 */
	Stream<PrimeRefIntfc> getPrimeRefStream(long skipCount, boolean preferParallel);

	/**
	 * Get prime ref associated with numerical index [0 based].
	 *
	 * @param primeIdx
	 * @return
	 */
	Optional<PrimeRefIntfc> getPrimeRef(@Min(0) long primeIdx);

	/**
	 * Get prime ref associated with the specified BigInteger
	 * if it is a prime.
	 *
	 * @param prime
	 * @return
	 */
	Optional<PrimeRefIntfc> getPrimeRef(@Min(0) BigInteger prime);

	/**
	 * Get the big integer [prime] from the prime ref associated with
	 * the specified numerical index [if exists].
	 *
	 * @param primeIdx
	 * @return
	 */
	Optional<BigInteger> getPrime(@Min(0) long primeIdx);
}
