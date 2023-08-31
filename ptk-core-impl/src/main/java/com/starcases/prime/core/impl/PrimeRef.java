package com.starcases.prime.core.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.starcases.prime.base.api.PrimeBaseIntfc;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;

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
public class PrimeRef implements PrimeRefFactoryIntfc
{
	private static final Logger LOG = Logger.getLogger(PrimeRef.class.getName());

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
	@Override
	public PrimeRefFactoryIntfc init(
			 @NonNull final Supplier<PrimeBaseIntfc> primeBaseSupplier
			)
	{
		primeBaseData = primeBaseSupplier.get();
		return this;
	}

	@Override
	public PrimeRefFactoryIntfc generateBases(@NonNull final Consumer<PrimeRefFactoryIntfc> basesGenerate)
	{
		basesGenerate.accept(this);
		return this;
	}

	@Override
	public PrimeBaseIntfc getPrimeBaseData()
	{
		if (primeBaseData == null)
		{
			LOG.info("Prime base data is null :  returning null from getPrimeBaseData()");
		}

		return primeBaseData;
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

	@SuppressWarnings("PMD.ConfusingTernary")
	@Override
	public boolean equals(final Object obj)
	{
		boolean ret;
		if (this == obj)
		{
			ret = true;
		}
		else if (obj == null)
		{
			ret = false;
		}
		else if (getClass() != obj.getClass())
		{
			ret = false;
		}
		else
		{
			final PrimeRef other = (PrimeRef) obj;
			ret = primeIdx == other.primeIdx;
		}
		return ret;
	}

	@Override
	public Optional<PrimeRefIntfc> getNextPrimeRef()
	{
		final var ret = primeSrc.getPrimeRefForIdx(primeIdx +1);
		return ret;
	}

	@Override
	public Optional<PrimeRefIntfc> getPrevPrimeRef()
	{
		return primeIdx <= 0 ? Optional.empty() : primeSrc.getPrimeRefForIdx(primeIdx -1);
		//return primeSrc.getPrimeRefForIdx(primeIdx -1);
	}

	@Override
	public long getPrime()
	{
		return primeSrc.getPrimeForIdx(primeIdx)
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
	@Override
	public OptionalLong getDistToNextPrime()
	{
		final var result = getNextPrimeRef();
		return  result.isPresent() ? OptionalLong.of(result.get().getPrime() - getPrime()) : OptionalLong.empty();
	}

	/**
	 * absolute value of difference with prev Prime
	 * if the prev Prime is known/exists.
	 *
	 * empty optional if prev Prime is unknown/doesn't exist
	 */
	@Override
	public OptionalLong getDistToPrevPrime()
	{
		final var result = getPrevPrimeRef();
		return result.isPresent() ? OptionalLong.of(result.get().getPrime() - getPrime()) : OptionalLong.empty();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(primeIdx);
	}

	@Override
	public String toString()
	{
		return Long.toString(this.getPrime());
	}

}