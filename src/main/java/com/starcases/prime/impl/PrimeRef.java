package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Default Prime representation.
 *
* The general algorithm idea is that the next Prime is derived from
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
	private static transient PrimeSourceIntfc primeSrc;

	public static void setPrimeSource(@NonNull PrimeSourceIntfc primeSrcIntfc)
	{
		primeSrc = primeSrcIntfc;
	}

	/*
	 *  Index for this instance of a Prime.
	 *  index to bitsets or collections for this val
	 */
	@Setter
	@Getter
	@ProtoField(number=1, defaultValue="0")
	public long primeIdx;


	private PrimeBaseIntfc primeBaseData = null;

	/**
	 * Handle simple Prime where the base is simply itself - i.e. 1, 2
	 * Simplifies bootstrapping
	 *
	 * @param Prime
	 */
	@ProtoFactory()
	public PrimeRef(long primeIdx)
	{
		this.primeIdx = primeIdx;
	}

	public PrimeRef init(
			@NonNull Supplier<PrimeBaseIntfc> primeBaseSupplier,
			@NonNull Set<BigInteger> primeBases)
	{
		primeBaseData = primeBaseSupplier.get();
		getPrimeBaseData().addPrimeBases(primeBases);
		return this;
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
		return primeSrc.getPrime(primeIdx)
				.orElseThrow();
	}

	@Override
	public PrimeBaseIntfc getPrimeBaseData()
	{
		return this.primeBaseData;
	}

	@Override
	public long getPrimeRefIdx()
	{
		return this.primeIdx;
	}

	/**
	 * absolute value of difference with next Prime
	 * if the next Prime is known.
	 *
	 * empty optional if next Prime is unknown/not calculated
	 */
	@Override
	public Optional<BigInteger> getDistToNextPrime()
	{
		return  getNextPrimeRef().map(npr -> npr.getPrime().subtract(getPrime()));
	}

	/**
	 * absolute value of difference with prev Prime
	 * if the prev Prime is known/exists.
	 *
	 * empty optional if prev Prime is unknown/doesn't exist
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