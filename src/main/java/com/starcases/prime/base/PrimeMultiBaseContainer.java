package com.starcases.prime.base;

import java.util.Map;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

import com.starcases.prime.intfc.BaseMetadataIntfc;
import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * represents a "base" of primes and acts as a container for
 * the items that represent the prime.
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class PrimeMultiBaseContainer implements PrimeBaseIntfc
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
	 * optional meta data regarding base.
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private Map<BaseTypes, BaseMetadataIntfc> baseMetadata = new ConcurrentHashMap<>();

	/**
	 * Assign ref of prime src for lookup of prime/primeref's.
	 * Unused currently.
	 * @param primeSrcIntfc
	 */
	public static void setPrimeSource(@NonNull final PrimeSourceIntfc primeSrcIntfc)
	{
		// unused right now
	}

	/**
	 * Get optional meta data regarding a base.
	 */
	@Override
	public BaseMetadataIntfc getBaseMetadata(final BaseTypes baseType)
	{
		return baseMetadata.getOrDefault(baseType, null);
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
							v = MutableListFactoryImpl.INSTANCE.empty();
						}

						final var vTmp = v;
						primeBase.stream().forEach(vTmp::add);
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
