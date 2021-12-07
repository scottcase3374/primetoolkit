package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Optional;

public interface PrimeRefIntfc
{
	BigInteger getPrime();
	
	BitSet getPrimeBaseIdxs();
	BitSet getPrimeBaseIdxs(BaseTypes baseType);
	
	BigInteger getMinPrimeBase();
	BigInteger getMaxPrimeBase();

	BigInteger getMinPrimeBase(BaseTypes baseType);
	BigInteger getMaxPrimeBase(BaseTypes baseType);

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
	void addPrimeBase(BitSet primeBase);
	void addPrimeBase(BitSet primeBase, BaseTypes baseType);
	
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
	String getIndexes(BaseTypes baseType);
	String getIdxPrimes(BaseTypes baseType);

}
