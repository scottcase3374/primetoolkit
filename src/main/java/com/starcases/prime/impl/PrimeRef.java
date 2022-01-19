package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.BitSet;

import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

/**
 * Default prime representation.
 *
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
	@Min(0)
	private int primeIdx;

	@NonNull
	// Represents sets of base primes that sum to this prime. (index to primes)
	private final Map<BaseTypes, List<Integer>> primeBaseIdxs = new EnumMap<>(BaseTypes.class);

	@NonNull
	private static PrimeSourceIntfc primeSrc;

	/**
	 * Handle simple prime where the base is simply itself - i.e. 1, 2
	 * Simplifies bootstrapping
	 *
	 * @param prime
	 */
	PrimeRef(@Min(0) @Max(2) int primeIdx, @NonNull BitSet primeBaseIdxs)
	{
		this.primeIdx = primeIdx;

		addPrimeBase(primeBaseIdxs);
	}

	public static void setPrimeSource(@NonNull PrimeSourceIntfc primeSrcIntfc)
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
	public Optional<PrimeRefIntfc> getPrimeRefWithinOffset(@NonNull BigInteger targetOffset)
	{
		return primeSrc.getPrimeRefWithinOffset(this.primeIdx, targetOffset);
	}

	@Override
	public BigInteger getMinPrimeBase()
	{
		return primeBaseIdxs
				.get(PrimeRef.primeSrc.getActiveBaseId())
				.stream()
				.map(i -> primeSrc.getPrime(i))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.min(bigIntComp)
				.orElse(BigInteger.ZERO);
	}

	@Override
	public BigInteger getMaxPrimeBase()
	{
		return primeBaseIdxs.get(PrimeRef.primeSrc.getActiveBaseId()).stream().map(i -> primeSrc.getPrime(i).get()).max(bigIntComp).get();
	}

	@Override
	public BigInteger getMinPrimeBase(@NonNull BaseTypes baseType)
	{
		return primeBaseIdxs.get(baseType).stream().map(i -> primeSrc.getPrime(i).get()).min(bigIntComp).get();
	}

	@Override
	public BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType)
	{
		return primeBaseIdxs
				.get(baseType)
				.stream()
				.map(i -> primeSrc.getPrime(i).orElse(BigInteger.ZERO))
				.max(bigIntComp)
				.orElse(BigInteger.ZERO);
	}

	@Override
	public BigInteger getPrime() {
		return primeSrc.getPrime(primeIdx).get();
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
	public List<BitSet> getPrimeBaseIdxs()
	{
		return getPrimeBaseIdxs(PrimeRef.primeSrc.getActiveBaseId());
	}

	@Override
	public List<BitSet> getPrimeBaseIdxs(@NonNull BaseTypes baseType) {
		var b = new BitSet();
		primeBaseIdxs.get(baseType).stream().forEach(b::set);
		return List.of(b);
	}

	/**
	 * Include a set of primes in the set of prime bases for the current prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBase(@NonNull BitSet primeBase)
	{
		this.primeBaseIdxs
			.merge(
					primeSrc.getActiveBaseId(),
					primeBase.stream().boxed().toList(),
					(a,b) -> { a.addAll(b); return a; } );
	}

	@Override
	public void addPrimeBase(@NonNull BitSet primeBase, @NonNull BaseTypes baseType)
	{
		this.primeBaseIdxs.merge(baseType, primeBase.stream().boxed().toList(), (a,b) -> b );
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