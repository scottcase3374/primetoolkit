package com.starcases.prime.base.impl;

import java.util.Map;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

import com.starcases.prime.base.api.BaseMetadataIntfc;
import com.starcases.prime.base.api.PrimeBaseIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;

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
	public BaseMetadataIntfc getBaseMetadata(final BaseTypesIntfc baseType)
	{
		return baseMetadata.getOrDefault(baseType, null);
	}

	/**
	 * Include a set of primes in the set of Prime bases for the current Prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBases(@NonNull final BaseTypesIntfc baseType, @NonNull final MutableList<ImmutableLongCollection> primeBase, final BaseMetadataIntfc baseMetadata)
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

		this.baseMetadata.computeIfAbsent(baseType, a -> baseMetadata);
	}

	@Override
	public void addPrimeBases(@NonNull final ImmutableLongCollection primeBase, @NonNull final BaseTypesIntfc baseType)
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

	}

	/**
	 * Include a set of primes in the set of Prime bases for the current Prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBases(@NonNull final MutableList<ImmutableLongCollection> primeBase)
	{
		addPrimeBases(BaseTypes.DEFAULT, primeBase, null);
	}

	@Override
	public void addPrimeBases(@NonNull final MutableList<ImmutableLongCollection> primeBase, @NonNull final BaseTypesIntfc baseType)
	{
		addPrimeBases(baseType, primeBase, null);
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
	public MutableList<ImmutableLongCollection> getPrimeBases(@NonNull final BaseTypesIntfc baseType)
	{
		return primeBases.getOrDefault(baseType, MutableListFactoryImpl.INSTANCE.empty());
	}
}
