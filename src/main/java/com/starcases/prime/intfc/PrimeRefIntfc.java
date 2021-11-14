package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.BitSet;

public interface PrimeRefIntfc
{
	BigInteger getPrime();
	
	BitSet getPrimeBaseIdxs();
	
	/**
	 * 
	 * @return int representing representing index in 
	 * overall list of primes.
	 */
	int getPrimeRefIdx();
	
	/**
	 * Utility method - not useful in initial context but
	 * desirable for research into alternative bases of a 
	 * prime.
	 * 
	 * @param primeBase
	 */
	void addPrimeBase(BitSet primeBase);
	
	/**
	 * Strings formatted for easy display
	 * @return
	 */
	String getIndexes();
	String getIdxPrimes();
}
