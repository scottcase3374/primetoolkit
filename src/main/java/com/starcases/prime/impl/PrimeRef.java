package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

/**
* The general algorithm idea is that the next prime is derived from 
* the sum of some subset of previous primes.  
* 
**/

public class PrimeRef extends AbstractPrimeRef implements PrimeRefIntfc
{
	/*
	 *  Index for this instance of a prime.
	 *  index to bitsets or collections for this val
	 */
	private int primeIdx;
	
	// Represents sets of base primes that sum to this prime. (index to primes)
	private Map<BaseTypes,List<Integer>> primeBaseIdxs = new HashMap<>(); 
	
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
	
	public int getPrimeRefIdx()
	{
		return this.primeIdx;
	}
	
	public Optional<PrimeRefIntfc> getNextPrimeRef()
	{
		return primeSrc.getPrimeRef(primeIdx +1);
	}
	
	public Optional<PrimeRefIntfc> getPrevPrimeRef()
	{
		if (primeIdx == 0)
			return Optional.empty();
		
		return primeSrc.getPrimeRef(primeIdx -1);
	}

	@Override
	public BigInteger getMinPrimeBase()
	{
		return primeBaseIdxs.get(PrimeRef.primeSrc.getActiveBaseId()).stream().map(i -> primeSrc.getPrime(i)).min(bigIntComp).get();
	}
	
	@Override
	public BigInteger getMaxPrimeBase()
	{
		return primeBaseIdxs.get(PrimeRef.primeSrc.getActiveBaseId()).stream().map(i -> primeSrc.getPrime(i)).max(bigIntComp).get();
	}

	@Override
	public BigInteger getMinPrimeBase(BaseTypes baseType)
	{
		return primeBaseIdxs.get(baseType).stream().map(i -> primeSrc.getPrime(i)).min(bigIntComp).get();
	}

	@Override
	public BigInteger getMaxPrimeBase(BaseTypes baseType)
	{
		return primeBaseIdxs.get(baseType).stream().map(i -> primeSrc.getPrime(i)).max(bigIntComp).get();
	}
	
	@Override
	public BigInteger getPrime() {
		return primeSrc.getPrime(primeIdx);
	}
	
	/**
	 * Returns the number of bases used to sum to the
	 * current prime.
	 */
	@Override
	public int getBaseSize()
	{
		 return primeBaseIdxs.get(BaseTypes.DEFAULT).size();
	}
	
	@Override
	public BitSet getPrimeBaseIdxs() {
		var b = new BitSet();
		primeBaseIdxs.get(PrimeRef.primeSrc.getActiveBaseId()).stream().forEach(b::set);
		return b;
	}	
	
	@Override
	public BitSet getPrimeBaseIdxs(BaseTypes baseType) {
		var b = new BitSet();
		primeBaseIdxs.get(baseType).stream().forEach(b::set);
		return b;
	}	
	
	/**
	 * Include a set of primes in the set of prime bases for the current prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBase(BitSet primeBase)
	{
		this.primeBaseIdxs.merge(primeSrc.getActiveBaseId(), primeBase.stream().boxed().toList(), (a,b) -> b );
	}

	@Override
	public void addPrimeBase(BitSet primeBase, BaseTypes baseType)
	{
		this.primeBaseIdxs.merge(baseType, primeBase.stream().boxed().toList(), (a,b) -> b );
	}	
	
	@Override
	public String getIndexes()
	{
		return this.getPrimeBaseIdxs()
						.stream()
						.boxed()
						.map(i -> Integer.toString(i)).
						collect(Collectors.joining(",","[", "]"));
	}

	@Override
	public String getIdxPrimes()
	{
		return getPrimeBaseIdxs()
				.stream()
				.boxed()
				.map(i -> primeSrc.getPrime(i).toString())
				.collect(Collectors.joining(",","[", "]"));
	}
	
	@Override
	public String getIndexes(BaseTypes baseType)
	{
		return this.getPrimeBaseIdxs(baseType)
				.stream()
				.boxed()
				.map(i -> Integer.toString(i)).
				collect(Collectors.joining(",","[", "]"));
	}
	
	@Override
	public String getIdxPrimes(BaseTypes baseType)
	{
		return getPrimeBaseIdxs(baseType)
				.stream()
				.boxed()
				.map(i -> primeSrc.getPrime(i).toString())
				.collect(Collectors.joining(",","[", "]"));	
	}
	
	public String toString()
	{
		return this.getPrime().toString();
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