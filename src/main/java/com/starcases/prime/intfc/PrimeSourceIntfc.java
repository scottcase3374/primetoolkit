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

	void store(BaseTypes ... baseTypes);
	void load(BaseTypes ... baseTypes);

	void setActiveBaseId(@NonNull BaseTypes activeBaseId);

	/**
	 * Iterator to PrimeRefIntfc instances.
	 *
	 * @return
	 */
	Iterator<PrimeRefIntfc> getPrimeRefIter();

	Stream<PrimeRefIntfc> getPrimeRefStream(boolean preferParallel);

	Optional<PrimeRefIntfc> getPrimeRef(@Min(0) long primeIdx);
	Optional<PrimeRefIntfc> getPrimeRef(@Min(0) BigInteger prime);
	Optional<BigInteger> getPrime(@Min(0) long primeIdx);
}
