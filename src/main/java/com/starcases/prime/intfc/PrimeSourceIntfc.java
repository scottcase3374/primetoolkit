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
	void setDisplayProgress(boolean doDisplay);
	void setDisplayPrimeTreeMetrics(boolean doDisplay);

	void init();
	void store(BaseTypes ... baseTypes);
	void load(BaseTypes ... baseTypes);

	/**
	 * Iterator to PrimeRefIntfc instances.
	 *
	 * @return
	 */
	Iterator<PrimeRefIntfc> getPrimeRefIter();

	Stream<PrimeRefIntfc> getPrimeRefStream(boolean preferParallel);
	Stream<PrimeRefIntfc> getPrimeRefStream(long skipCount, boolean preferParallel);

	Optional<PrimeRefIntfc> getPrimeRef(@Min(0) long primeIdx);
	Optional<PrimeRefIntfc> getPrimeRef(@Min(0) BigInteger prime);
	Optional<BigInteger> getPrime(@Min(0) long primeIdx);
}
