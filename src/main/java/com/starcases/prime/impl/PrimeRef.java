package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

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
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class PrimeRef implements PrimeRefIntfc
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Access lookup for prime/primeRefs
	 */
	@NonNull
	private static PrimeSourceIntfc primeSrc;

	/**
	 *  Index for this instance of a Prime.
	 *  index to bitsets or collections for this val
	 */
	@Setter
	@Getter
	public long primeIdx;

	/**
	 * Base data
	 */
	@Getter
	private PrimeBaseIntfc primeBaseData;

	/**
	 * Handle simple Prime where the base is simply itself - i.e. 1, 2
	 * Simplifies bootstrapping
	 *
	 * @param Prime
	 */
	public PrimeRef(final long primeIdx)
	{
		this.primeIdx = primeIdx;
	}

	/**
	 * alt constructor
	 * @param primeBaseSupplier
	 * @param primeBases
	 * @return
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	public PrimeRef init(
			@NonNull final Supplier<PrimeBaseIntfc> primeBaseSupplier,
			@NonNull final List<Set<BigInteger>> primeBases)
	{
		primeBaseData = primeBaseSupplier.get();
		getPrimeBaseData().addPrimeBases(primeBases);
		return this;
	}

	/**
	 * Assign the prime source reference for performing
	 * prime/primeref lookups.
	 *
	 * @param primeSrcIntfc
	 */
	public static void setPrimeSource(@NonNull final PrimeSourceIntfc primeSrcIntfc)
	{
		primeSrc = primeSrcIntfc;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}

		final PrimeRef other = (PrimeRef) obj;
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
		{
			return Optional.empty();
		}

		return primeSrc.getPrimeRef(primeIdx -1);
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public BigInteger getPrime()
	{
		return primeSrc.getPrime(primeIdx)
				.orElseThrow();
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
	@SuppressWarnings("PMD.LawOfDemeter")
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
	@SuppressWarnings("PMD.LawOfDemeter")
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