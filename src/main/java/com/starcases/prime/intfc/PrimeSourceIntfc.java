package com.starcases.prime.intfc;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import javax.validation.constraints.Min;

import com.starcases.prime.base.BaseTypes;

import lombok.NonNull;

/**
 *
 * Main interface for needs related to Prime / base generation and alternative algs.
 *
 * Implementations of this interface generally contain the primes data and directly data
 * directly related to the primes.  This allows near direct access to the individual primes
 * and data for navigating amongst the primes.
 *
 */
public interface PrimeSourceIntfc extends Serializable
{
	void init();

	void setActiveBaseId(@NonNull BaseTypes activeBaseId);

	/**
	 * Iterator to PrimeRefIntfc instances.
	 *
	 * @return
	 */
	Iterator<PrimeRefIntfc> getPrimeRefIter();

	Stream<PrimeRefIntfc> getPrimeRefStream(boolean preferParallel);

	/**
	 * Get exact Prime ref instance for value if it is a Prime and was previously identified and a Prime ref was created.
	 *
	 * @param val Must be positive value.

	 * @return
	 */
	Optional<PrimeRefIntfc> getPrimeRef(@NonNull @Min(1) BigInteger val);

	int getMaxIdx();

	Optional<PrimeRefIntfc> getPrimeRef(@Min(0) int primeIdx);
	Optional<BigInteger> getPrime(@Min(0) int primeIdx);
	int getPrimeIdx(@NonNull @Min(1) BigInteger val);
}
