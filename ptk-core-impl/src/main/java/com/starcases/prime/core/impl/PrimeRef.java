package com.starcases.prime.core.impl;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.mutable.MutableMapFactoryImpl;
import org.mapdb.HTreeMap;

import com.starcases.prime.base.api.PrimeBaseIntfc;
import com.starcases.prime.base.impl.BaseTypes;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;

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
	/**
	 * Access lookup for prime/primeRefs
	 */
	@NonNull
	private static PrimeSourceIntfc primeSrc;

	private static MutableMap<BaseTypesIntfc, HTreeMap<Long, long[]>> primeBases = MutableMapFactoryImpl.INSTANCE.empty();

	private final long primeIdx;



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
		return this;
	}


	/**
	 * For DEFAULT base type
	 */
	@Override
	public long[] getPrimeBases()
	{
		return getPrimeBases(BaseTypes.DEFAULT);
	}

	@Override
	public long[] getPrimeBases(@NonNull final BaseTypesIntfc baseType)
	{
		return primeBases.get(baseType).get(this.primeIdx);
	}

	@Override
	public PrimeRefFactoryIntfc generateBases(@NonNull final Consumer<PrimeRefFactoryIntfc> basesGenerate)
	{
		basesGenerate.accept(this);
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
	public Optional<PrimeRefIntfc> getNextPrimeRef()
	{
		return primeSrc.getPrimeRefForIdx(primeIdx+1);
	}

	@Override
	public Optional<PrimeRefIntfc> getPrevPrimeRef()
	{
		return primeIdx > 0 ? primeSrc.getPrimeRefForIdx(primeIdx-1) : Optional.empty();
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
	public void addPrimeBases(@NonNull final BaseTypesIntfc baseType, @NonNull final LongIterable primeBase)
	{
		primeBases.get(baseType).computeIfAbsent(this.primeIdx, (k) -> primeBase.toArray());
	}

	@Override
	public void addPrimeBases(@NonNull final BaseTypesIntfc baseType, @NonNull final long [] primeBase)
	{
		primeBases.get(baseType).computeIfAbsent(this.primeIdx, (k) -> primeBase);
	}

	@Override
	public void addPrimeBases(@NonNull final BaseTypesIntfc baseType, @NonNull final PrimeRefIntfc [] primeBase)
	{
		primeBases.get(baseType).computeIfAbsent(this.primeIdx, (k) ->
			com.starcases.prime.kern.api.Arrays.longArrayToLongArray((Long[])Arrays.asList(primeBase).stream().map(bref -> bref.getPrime()).toArray()));
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

	public static void setPrimeBases(BaseTypesIntfc baseType, HTreeMap<Long, long[]> primeBase)
	{
		primeBases.putIfAbsent(baseType, primeBase);
	}
}