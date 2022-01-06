package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Optional;

import lombok.NonNull;

/**
 * 
 * Main interface for working with the primes and accessing the info on bases.
 *
 */
public interface PrimeRefIntfc
{
	static final Comparator<PrimeRefIntfc> primeRefComparator = (PrimeRefIntfc o1, PrimeRefIntfc o2) -> o1.getPrime().compareTo(o2.getPrime());
	
	BigInteger getPrime();
	
	BitSet getPrimeBaseIdxs();
	BitSet getPrimeBaseIdxs(@NonNull BaseTypes baseType);
	
	BigInteger getMinPrimeBase();
	BigInteger getMaxPrimeBase();

	BigInteger getMinPrimeBase(@NonNull BaseTypes baseType);
	BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType);

	/**
	 * 
	 * @return int representing representing index in 
	 * overall list of primes.
	 */
	int getPrimeRefIdx();
	
	
	int getBaseSize();
	
	Optional<PrimeRefIntfc> getNextPrimeRef();
	Optional<PrimeRefIntfc> getPrevPrimeRef();
	
	Optional<BigInteger> getDistToNextPrime();
	Optional<BigInteger> getDistToPrevPrime();
	
	/**
	 * Utility method - not useful in initial context but
	 * desirable for research into alternative bases of a 
	 * prime.
	 * 
	 * @param primeBase
	 */
	void addPrimeBase(@NonNull BitSet primeBase);
	void addPrimeBase(@NonNull BitSet primeBase, @NonNull BaseTypes baseType);

	
	Optional<PrimeRefIntfc> getPrimeRefWithinOffset(@NonNull BigInteger targetOffset);
	
	/**
	 * Strings formatted for easy display
	 * @return
	 */
	String getIndexes();
	String getIdxPrimes();

	/**
	 * Strings formatted for easy display
	 * @return
	 */
	String getIndexes(@NonNull BaseTypes baseType);
	String getIdxPrimes(@NonNull BaseTypes baseType);
}
