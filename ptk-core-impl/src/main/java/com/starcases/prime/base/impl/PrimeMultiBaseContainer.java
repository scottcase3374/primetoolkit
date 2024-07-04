package com.starcases.prime.base.impl;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

import com.starcases.prime.base.api.BaseMetadataIntfc;
import com.starcases.prime.base.api.PrimeBaseIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * represents a "base" of primes and acts as a container for
 * the items that represent the prime.
 */
public class PrimeMultiBaseContainer implements PrimeBaseIntfc
{
	/**
	 * Represents sets of base primes that sum to this Prime. (index to primes)
	 *
	 */
	@NonNull
	private final Map<BaseTypesIntfc, MutableList<ImmutableLongCollection>> primeBases = new ConcurrentHashMap<>();

	/**
	 * Optional Metadata regarding base types of interest
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private Map<BaseTypesIntfc, BaseMetadataIntfc> baseMetadata = new ConcurrentHashMap<>();

	/**
	 * Get optional meta data regarding a base.
	 */
	@Override
	public BaseMetadataIntfc getBaseMetadata(@NotNull final BaseTypesIntfc baseType)
	{
		return baseMetadata.getOrDefault(baseType, null);
	}

	/**
	 * Include a set of primes in the set of Prime bases for the current Prime.
	 * @param primeBase Collection of prime bases.
	 */
	@Override
	public void addPrimeBases(@Min(0) long primeIdx, @NotNull final BaseTypesIntfc baseType, @NotNull final MutableList<ImmutableLongCollection> primeBase, final BaseMetadataIntfc baseMetadata)
	{
		this.primeBases.compute(baseType,
				(k, v) ->
					{
						if (v == null)
						{
							v = MutableListFactoryImpl.INSTANCE.of();
						}

						v.addAll(primeBase);
						return v;
					});
		//this.primeIdx = primeIdx;
		this.baseMetadata.computeIfAbsent(baseType, a -> baseMetadata);
	}

	@Override
	public void addPrimeBases(@Min(0) long primeIdx, @NotNull final ImmutableLongCollection primeBase, @NotNull final BaseTypesIntfc baseType)
	{
		this.primeBases.compute(baseType,
				(k, v) ->
					{
						if (v == null)
						{
							v = MutableListFactoryImpl.INSTANCE.of();
						}

						v.add(primeBase);
						return v;
					});
		//this.primeIdx = primeIdx;

	}

	/**
	 * Include a set of primes in the set of Prime bases for the current Prime.
	 * @param primeBase Collection of prime bases.
	 */
	@Override
	public void addPrimeBases(@Min(0) long primeIdx, @NotNull final MutableList<ImmutableLongCollection> primeBase)
	{
		addPrimeBases(primeIdx, BaseTypes.DEFAULT, primeBase, null);
	}

	@Override
	public void addPrimeBases(@Min(0) long primeIdx, @NotNull final MutableList<ImmutableLongCollection> primeBase, @NotNull final BaseTypesIntfc baseType)
	{
		addPrimeBases(primeIdx, baseType, primeBase, null);
	}

	/**
	 * For DEFAULT base type
	 */
	@Override
	public MutableList<ImmutableLongCollection> getPrimeBases()
	{
		return getPrimeBases(BaseTypes.DEFAULT);
	}

	@Override
	public MutableList<ImmutableLongCollection> getPrimeBases(@NotNull final BaseTypesIntfc baseType)
	{
		return primeBases.getOrDefault(baseType, MutableListFactoryImpl.INSTANCE.empty());
	}

	@Override
	public void addPrimeBases(long primeIdx, @NotNull long[] primeBase, @NotNull BaseTypesIntfc baseType)
	{
		// TODO Auto-generated method stub

	}
}
