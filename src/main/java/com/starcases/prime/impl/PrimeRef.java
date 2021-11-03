package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Objects;
import java.util.stream.Stream;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

/**
* The general algorithm idea is that the next prime is derived from 
* the sum of some subset of previous primes.  
* 
**/

public class PrimeRef implements PrimeRefIntfc
{
	//
	// Instance data
	//
	
	// This instance of a prime
	int primeIdx; // index to bitsets or collections for this val
	
	// Represents sets of base primes that sum to this prime. (index to primes)
	ArrayList<BitSet> primeBaseIdxs = new ArrayList<>(); 
	
	static PrimeSourceIntfc primeSrc;
	
	/**
	 * Handle simple prime where the base is simply itself - i.e. 1, 2 
	 * Simplifies bootstrapping
	 * 
	 * @param prime
	 */
	PrimeRef(int primeIdx, BitSet primeBaseIdxs)
	{
		this.primeIdx = primeIdx;
		
		addPrimeBase(primeBaseIdxs);
	} 
	
	PrimeRef(int primeIdx)
	{
		this.primeIdx = primeIdx;
				
		addPrimeBase(BitSet.valueOf(new byte[] {(byte)primeIdx}));
	}

	public static void setPrimeSource(PrimeSourceIntfc primeSrcIntfc)
	{
		primeSrc = primeSrcIntfc;
	}
	
	public PrimeRefIntfc getPrimeRef()
	{
		return this;
	}
	
	public int getPrimeRefIdx()
	{
		return this.primeIdx;
	}
	
	@Override
	public BigInteger getPrime() {
		return primeSrc.getPrime(primeIdx);
	}
	
	@Override
	public Stream<BitSet> getPrimeBaseIdxs() {
		return primeBaseIdxs.stream();
	}	
	
	/**
	 * Include a set of primes in the set of prime bases for the current prime.
	 * @param primeBase
	 */
	public void addPrimeBase(BitSet primeBase)
	{
		this.primeBaseIdxs.add(primeBase);
	}

	@Override
	public int hashCode() {
		return Objects.hash(primeIdx);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrimeRef other = (PrimeRef) obj;
		return primeIdx == other.primeIdx;
	}
}