package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.stream.Stream;

public interface PrimeRefIntfc
{
	Stream<BitSet> getPrimeBaseIdxs();
	
	void addPrimeBase(BitSet primeBase);
	
	BigInteger getPrime();
	
	/**
	 * 
	 * @return int representing the bit within bits for this item
	 */
	int getPrimeRefIdx();
}
