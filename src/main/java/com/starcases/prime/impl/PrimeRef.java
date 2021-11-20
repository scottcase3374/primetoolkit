package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

/**
* The general algorithm idea is that the next prime is derived from 
* the sum of some subset of previous primes.  
* 
**/

public class PrimeRef implements PrimeRefIntfc
{
	/*
	 *  Index for this instance of a prime.
	 *  index to bitsets or collections for this val
	 */
	private int primeIdx;
	
	// Represents sets of base primes that sum to this prime. (index to primes)
	private ArrayList<List<Integer>> primeBaseIdxs = new ArrayList<>(); 
	
	private static PrimeSourceIntfc primeSrc;
	
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
	
	public PrimeRefIntfc getNextPrimeRef()
	{
		return primeSrc.getPrimeRef(primeIdx +1);
	}
	
	public PrimeRefIntfc getPrevPrimeRef()
	{
		return primeSrc.getPrimeRef(primeIdx -1);
	}
	
	@Override
	public BigInteger getPrime() {
		return primeSrc.getPrime(primeIdx);
	}
	
	@Override
	public BitSet getPrimeBaseIdxs() {
		BitSet b = new BitSet();
		primeBaseIdxs.get(PrimeRef.primeSrc.getActiveBaseId()).stream().forEach(b::set);
		return b;
	}	
	
	/**
	 * Include a set of primes in the set of prime bases for the current prime.
	 * @param primeBase
	 */
	public void addPrimeBase(BitSet primeBase)
	{
		this.primeBaseIdxs.add(primeBase.stream().boxed().collect(Collectors.toList()));
	}

	public String getIndexes()
	{
		return this.getPrimeBaseIdxs()
						.stream()
						.boxed()
						.map(i -> Integer.toString(i)).
						collect(Collectors.joining(",","[", "]"));
	}

	public String getIdxPrimes()
	{
		return getPrimeBaseIdxs()
				.stream()
				.boxed()
				.map(i -> primeSrc.getPrime(i).toString())
				.collect(Collectors.joining(",","[", "]"));
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