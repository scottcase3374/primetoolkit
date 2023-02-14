package com.starcases.prime.intfc;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.LongSupplier;
import java.util.stream.Stream;

import javax.validation.constraints.Min;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.impl.GenerationProgress;
import com.starcases.prime.preload.PrePrimed;

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
public interface PrimeSourceIntfc extends Serializable
{
	/**
	 * output progress of initial base creation.
	 * @param doDisplay
	 */
	void setDisplayProgress(GenerationProgress progress);

	/**
	 * set flag indicating whether to output metrics regarding the
	 * prime trees generated for initial base.
	 * @param doDisplay
	 */
	void setDisplayPrimeTreeMetrics(boolean doDisplay);

	/**
	 * DefaultInit base info.
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
	void load(PrePrimed prePrimed,  BaseTypes ... baseTypes);

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
	Optional<PrimeRefIntfc> getPrimeRefForIdx(@Min(0) long primeIdx);

	/**
	 * Get prime ref associated with the specified long
	 * if it is a prime.
	 *
	 * @param prime
	 * @return
	 */
	Optional<PrimeRefIntfc> getPrimeRefForPrime(@Min(0) long prime);

	/**
	 * Get prime ref associated with the long produced by the supplier
	 * if it is a prime.
	 *
	 * @param longSupplier
	 * @return
	 */
	Optional<PrimeRefIntfc> getPrimeRefForPrime(final LongSupplier longSupplier);

	/**
	 * Get the big integer [prime] from the prime ref associated with
	 * the specified numerical index [if exists].
	 *
	 * @param primeIdx
	 * @return
	 */
	OptionalLong getPrimeForIdx(@Min(0) long primeIdx);
}
