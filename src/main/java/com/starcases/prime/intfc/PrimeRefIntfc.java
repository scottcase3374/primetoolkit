package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.starcases.prime.base.BaseTypes;

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

	//
	// Prime base info
	//

	/**
	 * Not every use case needs multiple bases per prime; the interface
	 * supports it but it is only implemented to actually include
	 * multiple bases per prime in certain implementations.
	 * @return
	 */
	List<BitSet> getPrimeBaseIdxs();

	/**
	 * Not every use case needs multiple bases per prime; the interface
	 * supports it but it is only implemented to actually include
	 * multiple bases per prime in certain implementations.
	 * @return
	 */
	List<BitSet> getPrimeBaseIdxs(@NonNull BaseTypes baseType);

	BigInteger getMinPrimeBase();
	BigInteger getMaxPrimeBase();

	BigInteger getMinPrimeBase(@NonNull BaseTypes baseType);
	BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType);

	int getBaseSize();

	/**
	 * Utility method - not useful in initial context but
	 * desirable for research into alternative bases of a
	 * prime.
	 *
	 * @param primeBase
	 */
	void addPrimeBase(@NonNull BitSet primeBase);
	void addPrimeBase(@NonNull BitSet primeBase, @NonNull BaseTypes baseType);
}
