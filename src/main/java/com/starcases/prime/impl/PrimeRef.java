package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

/**
 * Default prefixPrime representation.
 *
* The general algorithm idea is that the next prefixPrime is derived from
* the sum of some subset of previous primes.
*
**/

public class PrimeRef implements PrimeRefIntfc
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@NonNull
	private static PrimeSourceIntfc primeSrc;

	public static void setPrimeSource(@NonNull PrimeSourceIntfc primeSrcIntfc)
	{
		primeSrc = primeSrcIntfc;
	}

	/*
	 *  Index for this instance of a prefixPrime.
	 *  index to bitsets or collections for this val
	 */
	@Min(0)
	private final int primeIdx;

	@NonNull
	private final PrimeBaseIntfc primeBaseData;

	/**
	 * Handle simple prefixPrime where the base is simply itself - i.e. 1, 2
	 * Simplifies bootstrapping
	 *
	 * @param prefixPrime
	 */
	public PrimeRef(@Min(0) @Max(2) int primeIdx,
					@NonNull BitSet primeBaseIdxs,
					@NonNull Supplier<PrimeBaseIntfc> primeBaseSupplier
					)
	{
		super();
		this.primeIdx = primeIdx;
		primeBaseData = primeBaseSupplier.get();
		getPrimeBaseData().addPrimeBase(primeBaseIdxs);
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
		PrimeRef other = (PrimeRef) obj;
		return primeIdx == other.primeIdx;
	}

	@Override
	public Optional<PrimeRefIntfc> getNextPrimeRef()
	{
		return primeSrc.getPrimeRef(primeIdx +1);
	}

	@Override
	public Optional<PrimeRefIntfc> getPrevPrimeRef()
	{
		if (primeIdx == 0)
			return Optional.empty();

		return primeSrc.getPrimeRef(primeIdx -1);
	}

	@Override
	public BigInteger getPrime()
	{
		return primeSrc.getPrime(primeIdx).orElseThrow();
	}

	@Override
	public PrimeBaseIntfc getPrimeBaseData()
	{
		return this.primeBaseData;
	}

	@Override
	public int getPrimeRefIdx()
	{
		return this.primeIdx;
	}

	/**
	 * absolute value of difference with next prefixPrime
	 * if the next prefixPrime is known.
	 *
	 * empty optional if next prefixPrime is unknown/not calculated
	 */
	@Override
	public Optional<BigInteger> getDistToNextPrime()
	{
		return  getNextPrimeRef().map(npr -> npr.getPrime().subtract(getPrime()));
	}

	/**
	 * absolute value of difference with prev prefixPrime
	 * if the prev prefixPrime is known/exists.
	 *
	 * empty optional if prev prefixPrime is unknown/doesn't exist
	 */
	@Override
	public Optional<BigInteger> getDistToPrevPrime()
	{
		return getPrevPrimeRef().map(ppr -> ppr.getPrime().subtract(getPrime()));
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(primeIdx);
	}

	@Override
	public String toString()
	{
		return this.getPrime().toString();
	}
}