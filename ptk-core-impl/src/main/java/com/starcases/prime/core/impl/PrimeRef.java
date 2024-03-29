package com.starcases.prime.core.impl;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.starcases.prime.base.api.PrimeBaseIntfc;
import com.starcases.prime.base.impl.PrimeMultiBaseContainer;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;

import lombok.NonNull;

/**
 * Default Prime representation.
 *
* The general algorithm idea is that the next Prime is derived from
* the sum of some subset of previous primes.
*
**/
public class PrimeRef implements PrimeRefFactoryIntfc
{
	private static final Logger LOG = Logger.getLogger(PrimeRef.class.getName());

	/**
	 * Access lookup for prime/primeRefs
	 */
	@NonNull
	private static PrimeSourceIntfc primeSrc;

	private final long primeIdx;

	/**
	 * Base data
	 */
	private PrimeBaseIntfc primeBaseData = new PrimeMultiBaseContainer();

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

	public PrimeRef(
			final long primeIdx
			,@NonNull final Supplier<PrimeBaseIntfc> primeBaseSupplier
		)
	{
		this(primeIdx);
		primeBaseData = primeBaseSupplier.get();
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

	@Override
	public Optional<PrimeRefIntfc> getNextPrimeRef()
	{
		return primeSrc.getPrimeRefForIdx(primeIdx+1);
	}

	@Override
	public Optional<PrimeRefIntfc> getPrevPrimeRef()
	{
		return primeIdx > 0 ? Optional.empty() : primeSrc.getPrimeRefForIdx(primeIdx-1);
	}

	@Override
	public boolean hasNext()
	{
		return primeSrc.getPrimeRefForIdx(primeIdx+1).isPresent();
	}

	@Override
	public boolean hasPrev()
	{
		return primeIdx > 0 && primeSrc.getPrimeRefForIdx(primeIdx-1).isPresent();
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
		return  primeIdx;
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
		final var result = primeSrc.getPrimeRefForIdx(primeIdx);

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
		final var result =  primeSrc.getPrimeRefForIdx(primeIdx);

		return result.isPresent() ? OptionalLong.of(result.get().getPrime() - getPrime()) : OptionalLong.empty();
	}

	@Override
	public String toString()
	{
		return Long.toString(this.getPrime());
	}

	@Override
	public int hashCode()
	{
		return (int)primeIdx*31+5;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PrimeRef other = (PrimeRef) obj;
		return primeIdx == other.primeIdx;
	}
}