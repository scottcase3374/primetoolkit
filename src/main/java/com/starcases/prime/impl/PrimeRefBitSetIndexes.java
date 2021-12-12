package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Optional;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

/**
* 
*  Alternative Prime representation (of bases)- use bitset to represent bases which is more compact
*  sometimes for the data. Part of support to determine tradeoffs of different representations.
* 
**/

public class PrimeRefBitSetIndexes extends AbstractPrimeRef implements PrimeRefIntfc
{
	/*
	 *  Index for this instance of a prime.
	 *  index to bitsets or collections for this val
	 */
	private int primeIdx;
	
	// Represents sets of base primes that sum to this prime. (index to primes)
	private Map<BaseTypes, BitSet> primeBaseIdxs = new HashMap<>(); 
	
	private static PrimeSourceIntfc primeSrc;
	
	/**
	 * Handle simple prime where the base is simply itself - i.e. 1, 2 
	 * Simplifies bootstrapping
	 * 
	 * @param prime
	 */
	PrimeRefBitSetIndexes(int primeIdx, BitSet primeBaseIdxs)
	{
		this.primeIdx = primeIdx;
		
		addPrimeBase(primeBaseIdxs);
	} 

	public static void setPrimeSource(PrimeSourceIntfc primeSrcIntfc)
	{
		primeSrc = primeSrcIntfc;
	}
	

	
	@Override
	public int getBaseSize()
	{
		 return primeBaseIdxs.get(BaseTypes.DEFAULT).cardinality();
	}

	@Override
	public int getPrimeRefIdx()
	{
		return this.primeIdx;
	}
	
	@Override
	public BigInteger getPrime() 
	{
		return primeSrc.getPrime(primeIdx);
	}
	
	@Override
	public BitSet getPrimeBaseIdxs() 
	{
		return primeBaseIdxs.get(primeSrc.getActiveBaseId());
	}	

	@Override
	public BitSet getPrimeBaseIdxs(BaseTypes baseType) 
	{
		return primeBaseIdxs.get(baseType);
	}	
	
	/**
	 * Include a set of primes in the set of prime bases for the current prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBase(BitSet primeBase)
	{
		this.primeBaseIdxs.merge(primeSrc.getActiveBaseId(), primeBase, (a,b) -> b);
	}

	@Override
	public void addPrimeBase(BitSet primeBase, BaseTypes baseType)
	{
		this.primeBaseIdxs.merge(baseType, primeBase, (a,b) -> b);
	}	
	
	@Override
	public String getIndexes()
	{
		return getPrimeBaseIdxs().stream().boxed().map(i -> Integer.toString(i)).collect(Collectors.joining(",","[", "]"));
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
		return getPrimeBaseIdxs(baseType).stream().boxed().map(i -> Integer.toString(i)).collect(Collectors.joining(",","[", "]"));
	}

	@Override
	public BigInteger getMinPrimeBase()
	{
		return primeBaseIdxs.get(primeSrc.getActiveBaseId()).stream().boxed().map(i -> primeSrc.getPrime(i)).min(bigIntComp).get();
	}
	
	@Override
	public BigInteger getMaxPrimeBase()
	{
		return primeBaseIdxs.get(primeSrc.getActiveBaseId()).stream().boxed().map(i -> primeSrc.getPrime(i)).max(bigIntComp).get();
	}

	@Override
	public BigInteger getMinPrimeBase(BaseTypes baseType)
	{
		return primeBaseIdxs.get(baseType).stream().boxed().map(i -> primeSrc.getPrime(i)).min(bigIntComp).get();
	}

	@Override
	public BigInteger getMaxPrimeBase(BaseTypes baseType)
	{
		return primeBaseIdxs.get(baseType).stream().boxed().map(i -> primeSrc.getPrime(i)).max(bigIntComp).get();
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
	
	@Override
	public int hashCode() 
	{
		return Objects.hash(primeIdx);
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		var other = (PrimeRefBitSetIndexes) obj;
		return primeIdx == other.primeIdx;
	}

	@Override
	public Optional<PrimeRefIntfc> getNextPrimeRef() 
	{
		return primeSrc.getPrimeRef(primeIdx+1);
	}

	@Override
	public Optional<PrimeRefIntfc> getPrevPrimeRef() 
	{
		return primeSrc.getPrimeRef(primeIdx-1);
	}
}