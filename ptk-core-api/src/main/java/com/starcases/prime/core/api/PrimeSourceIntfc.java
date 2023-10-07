package com.starcases.prime.core.api;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.LongSupplier;
import java.util.stream.Stream;

import jakarta.validation.constraints.Min;
import lombok.NonNull;

/**
 *
 * Main interface for needs related to Prime / base
 * generation and alternative algs.
 *
 * Implementations of this interface generally contain the
 * primes data and data
 * directly related to the primes.  This allows near direct
 * access to the individual primes
 * and data for navigating amongst the primes.
 *
 */
public interface PrimeSourceIntfc extends Serializable
{


	/**
	 * Iterator to PrimeRefIntfc instances.
	 *
	 * @return
	 */
	Iterator<PrimeRefIntfc> getPrimeRefIter();

	/**
	 * Iterator to PrimeRefIntfc instances.
	 *
	 * @return
	 */
	Iterator<PrimeRefIntfc> getPrimeRefIter(long startIdx);

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
	Optional<PrimeRefIntfc> getPrimeRefForPrime(@NonNull final LongSupplier longSupplier);

	/**
	 * Get the big integer [prime] from the prime ref associated with
	 * the specified numerical index [if exists].
	 *
	 * @param primeIdx
	 * @return
	 */
	OptionalLong getPrimeForIdx(@Min(0) long primeIdx);

	/**
	 *
	 * @param nextPrimeIdx
	 * @param newPrime
	 * @param defaultBase
	 * @return
	 */
	PrimeRefFactoryIntfc addPrimeRef(
			@Min(0) final long nextPrimeIdx,
			@Min(1) final long newPrime
			);
}
