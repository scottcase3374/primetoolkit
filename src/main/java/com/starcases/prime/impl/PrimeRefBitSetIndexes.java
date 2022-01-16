package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.List;
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
*  This implementation also includes support for multiple bases per prime - useful when needing
*  to find multiple combinations of 3 primes that sum to a specific prime (see code related to triple).
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
	private final Map<BaseTypes, List<BitSet>> primeBaseIdxs = new EnumMap<>(BaseTypes.class);

	@NonNull
	private static PrimeSourceIntfc primeSrc;

	/**
	 * Handle simple prime where the base is simply itself - i.e. 1, 2
	 * Simplifies bootstrapping
	 *
	 * @param prime
	 */
	public PrimeRefBitSetIndexes(@Min(0) int primeIdx, @NonNull BitSet primeBaseIdxs)
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
		 return primeBaseIdxs.get(BaseTypes.DEFAULT).get(0).cardinality();
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
	public List<BitSet> getPrimeBaseIdxs()
	{
		return getPrimeBaseIdxs(primeSrc.getActiveBaseId());
	}

	@Override
	public List<BitSet> getPrimeBaseIdxs(@NonNull BaseTypes baseType)
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

		addPrimeBase(primeBase, primeSrc.getActiveBaseId());
	}

	@Override
	public void addPrimeBase(@NonNull BitSet primeBase, @NonNull BaseTypes baseType)
	{
		this.primeBaseIdxs.merge(baseType, new ArrayList<BitSet>(Arrays.asList(primeBase)), (a,b) -> { a.addAll(b); return a;} );
	}

	@Override
	public String getIndexes()
	{
		return getIndexes(primeSrc.getActiveBaseId());
	}

	@Override
	public String getIndexes(@NonNull BaseTypes baseType)
	{
		return getPrimeBaseIdxs(baseType)
					.stream()
					.map(bs -> bs.stream().boxed().map(i -> i.toString()).collect(Collectors.joining(",","[","]")))
					.collect(Collectors.joining(",","[", "]"));
	}

	@Override
	public String getIdxPrimes()
	{
		return getIdxPrimes(primeSrc.getActiveBaseId());
	}

	@Override
	public String getIdxPrimes(@NonNull BaseTypes baseType)
	{
		return getPrimeBaseIdxs(baseType)
				.stream()
				.map(bs -> bs.stream().boxed().map(i -> primeSrc.getPrime(i).get().toString()).collect(Collectors.joining(",","[","]")))
				.collect(Collectors.joining(", ","[", "]"));
	}

	@Override
	public BigInteger getMinPrimeBase()
	{
		return getMinPrimeBase(primeSrc.getActiveBaseId());
	}

	/**
	 * Need to think about how to handle multiple sets of bases for a single prime.  In that
	 * scenario, which base set should be used to determine the min prime base.  The
	 * current usage is just general reporting but the results should be consistent.
	 */
	@Override
	public BigInteger getMinPrimeBase(@NonNull BaseTypes baseType)
	{
		// Need to redo this; the result of Optional<Optional<>> is just silly.
		return primeBaseIdxs
				.get(baseType)
				.stream()
				.map(bs -> bs.stream().boxed().map(i -> primeSrc.getPrimeRef(i).get().getPrime()).min(bigIntComp))
				.findAny().get().get();
	}

	@Override
	public BigInteger getMaxPrimeBase()
	{
		return getMaxPrimeBase(primeSrc.getActiveBaseId());
	}

	/**
	 * Need to think about how to handle multiple sets of bases for a single prime.  In that
	 * scenario, which base set should be used to determine the max prime base.  The
	 * current usage is just general reporting but the results should be consistent.
	 */
	@Override
	public BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType)
	{
		// Need to redo this; the result of Optional<Optional<>> is just silly.
		return primeBaseIdxs
				.get(baseType)
				.stream()
				.map(bs -> bs.stream().boxed().map(i -> primeSrc.getPrimeRef(i).get().getPrime()).max(bigIntComp))
				.findAny().get().get();
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