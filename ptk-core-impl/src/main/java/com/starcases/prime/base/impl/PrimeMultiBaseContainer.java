package com.starcases.prime.base.impl;

import java.util.Map;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

import com.starcases.prime.base.api.BaseMetadataIntfc;
import com.starcases.prime.base.api.PrimeBaseIntfc;
import com.starcases.prime.cache.api.PersistedPrefixCacheIntfc;
import com.starcases.prime.kern.api.Arrays;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.kern.api.IdxToSubsetMapperIntfc;
import com.starcases.prime.kern.impl.IdxToSubsetMapperImpl;

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

	private final Map<BaseTypesIntfc, PersistedPrefixCacheIntfc> baseCaches = new ConcurrentHashMap<>();

	private static final IdxToSubsetMapperIntfc idxMap = new IdxToSubsetMapperImpl();
	private long primeIdx;

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
	public void addPrimeBases(@Min(0) long primeIdx, @NonNull final BaseTypesIntfc baseType, @NonNull final MutableList<ImmutableLongCollection> primeBase, final BaseMetadataIntfc baseMetadata)
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
		this.primeIdx = primeIdx;
		this.baseMetadata.computeIfAbsent(baseType, a -> baseMetadata);
	}

	@Override
	public void addPrimeBases(@Min(0) long primeIdx, @NonNull final ImmutableLongCollection primeBase, @NonNull final BaseTypesIntfc baseType)
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
		this.primeIdx = primeIdx;

	}

	/**
	 * Include a set of primes in the set of Prime bases for the current Prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBases(@Min(0) long primeIdx, @NonNull final MutableList<ImmutableLongCollection> primeBase)
	{
		addPrimeBases(primeIdx, BaseTypes.DEFAULT, primeBase, null);
	}

	@Override
	public void addPrimeBases(@Min(0) long primeIdx, @NonNull final MutableList<ImmutableLongCollection> primeBase, @NonNull final BaseTypesIntfc baseType)
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
	public MutableList<ImmutableLongCollection> getPrimeBases(@NonNull final BaseTypesIntfc baseType)
	{
		final long [] retSubset = {-1};
		final int [] retOffset = {-1};
		idxMap.convertIdxToSubsetAndOffset(this.primeIdx, retSubset, retOffset);

		MutableList<ImmutableLongCollection> ret = null;

		var cachedResult = this.baseCaches.get(baseType).get(retSubset[0]).get(retOffset[0]);
		if (cachedResult != null)
		{
			ret = MutableListFactoryImpl.INSTANCE.of(Arrays.arrayToImmutableLongColl(cachedResult));
		}
		return ret != null ? ret : primeBases.getOrDefault(baseType, MutableListFactoryImpl.INSTANCE.empty());
	}
}
