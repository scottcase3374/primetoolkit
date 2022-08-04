package com.starcases.prime.base;

import java.util.Map;

import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;

import com.starcases.prime.intfc.BaseMetadataIntfc;
import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 *  Default 'container' for base information.
 */
public class PrimeBaseContainer implements PrimeBaseIntfc
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Represents sets of base primes that sum to this Prime. (index to primes)
	 *
	 */
	@NonNull
	private final Map<BaseTypes, MutableList<ImmutableLongCollection>> primeBases = new ConcurrentHashMap<>();

	/**
	 * Optional Metadata regarding base types of interest
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private Map<BaseTypes, BaseMetadataIntfc> baseMetadata = new ConcurrentHashMap<>();

	/**
	 * Get optional meta data regarding a base.
	 */
	@Override
	public BaseMetadataIntfc getBaseMetadata(final BaseTypes baseType)
	{
		return baseMetadata.getOrDefault(baseType, null);
	}

	/**
	 * Assign prime source ref for prime/primeref lookups.
	 * @param primeSrcIntfc
	 */
	public static void setPrimeSource(@NonNull final PrimeSourceIntfc primeSrcIntfc)
	{
		// unused right now
	}

	/**
	 * Include a set of primes in the set of Prime bases for the current Prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBases(@NonNull final BaseTypes baseType, @NonNull final MutableList<ImmutableLongCollection> primeBase, final BaseMetadataIntfc baseMetadata)
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
	public void addPrimeBases(@NonNull final MutableList<ImmutableLongCollection> primeBase, @NonNull final BaseTypes baseType)
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
	public MutableList<ImmutableLongCollection> getPrimeBases(@NonNull final BaseTypes baseType)
	{
		return primeBases.getOrDefault(baseType, MutableListFactoryImpl.INSTANCE.empty());
	}
}
