package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Map;
import java.util.EnumMap;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import java.util.Optional;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

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
	@Min(0)
	private int primeIdx;
	
	// Represents sets of base primes that sum to this prime. (index to primes)
	@NonNull
	private final Map<BaseTypes, BitSet> primeBaseIdxs = new EnumMap<>(BaseTypes.class); 
	
	@NonNull
	private static PrimeSourceIntfc primeSrc;
	
	/**
	 * Handle simple prime where the base is simply itself - i.e. 1, 2 
	 * Simplifies bootstrapping
	 * 
	 * @param prime
	 */
	PrimeRefBitSetIndexes(@Min(0) int primeIdx, @NonNull BitSet primeBaseIdxs)
	{
		this.primeIdx = primeIdx;
		
		addPrimeBase(primeBaseIdxs);
	} 

	public static void setPrimeSource(@NonNull PrimeSourceIntfc primeSrcIntfc)
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
	public Optional<PrimeRefIntfc> getPrimeRefWithinOffset(@NonNull BigInteger targetOffset)
	{
		return primeSrc.getPrimeRefWithinOffset(this.primeIdx, targetOffset);
	}
	
	@Override
	public BigInteger getPrime() 
	{
		return primeSrc.getPrime(primeIdx).get();
	}
	
	@Override
	public BitSet getPrimeBaseIdxs() 
	{
		return primeBaseIdxs.get(primeSrc.getActiveBaseId());
	}	

	@Override
	public BitSet getPrimeBaseIdxs(@NonNull BaseTypes baseType) 
	{
		return primeBaseIdxs.get(baseType);
	}	
	
	/**
	 * Include a set of primes in the set of prime bases for the current prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBase(@NonNull BitSet primeBase)
	{
		this.primeBaseIdxs.merge(primeSrc.getActiveBaseId(), primeBase, (a,b) -> b);
	}

	@Override
	public void addPrimeBase(@NonNull BitSet primeBase, @NonNull BaseTypes baseType)
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
	public String getIndexes(@NonNull BaseTypes baseType)
	{
		return getPrimeBaseIdxs(baseType).stream().boxed().map(i -> Integer.toString(i)).collect(Collectors.joining(",","[", "]"));
	}

	@Override
	public BigInteger getMinPrimeBase()
	{
		return primeBaseIdxs.get(primeSrc.getActiveBaseId()).stream().boxed().map(i -> primeSrc.getPrime(i).get()).min(bigIntComp).get();
	}
	
	@Override
	public BigInteger getMaxPrimeBase()
	{
		return primeBaseIdxs.get(primeSrc.getActiveBaseId()).stream().boxed().map(i -> primeSrc.getPrime(i).get()).max(bigIntComp).get();
	}

	@Override
	public BigInteger getMinPrimeBase(@NonNull BaseTypes baseType)
	{
		return primeBaseIdxs.get(baseType).stream().boxed().map(i -> primeSrc.getPrime(i).get()).min(bigIntComp).get();
	}

	@Override
	public BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType)
	{
		return primeBaseIdxs.get(baseType).stream().boxed().map(i -> primeSrc.getPrime(i).get()).max(bigIntComp).get();
	}
	
	@Override
	public String getIdxPrimes(@NonNull BaseTypes baseType)
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