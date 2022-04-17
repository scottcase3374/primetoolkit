package com.starcases.prime.base;

import java.math.BigInteger;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.impl.list.mutable.FastList;
import org.infinispan.protostream.annotations.ProtoFactory;

import com.starcases.prime.intfc.BaseMetadataIntfc;
import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.Getter;
import lombok.NonNull;
// FIXME
public class PrimePrefixSuffixBaseContainer implements PrimeBaseIntfc
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

	/**
	 * Represents sets of base primes that sum to this Prime. (index to primes)
	 *
	 */
	@NonNull
	private final Map<BaseTypes, List<Set<BigInteger>>> primeBases = new EnumMap<>(BaseTypes.class);


	@Getter
	private BaseMetadataIntfc baseMetadata;

	@ProtoFactory
	public PrimePrefixSuffixBaseContainer()
	{
		// nothing to init here
	}

	/**
	 * Include a set of primes in the set of Prime bases for the current Prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBases(@NonNull BaseTypes baseType, @NonNull Set<BigInteger> primeBase, BaseMetadataIntfc baseMetadata)
	{
		this.primeBases.compute(baseType,
				(k, v) ->
					{
						if (v == null)
						{
							v = new FastList<Set<BigInteger>>(1);
						}

						v.add(primeBase);
						return v;
					});
		this.baseMetadata = baseMetadata;
	}

	/**
	 * Include a set of primes in the set of Prime bases for the current Prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBases(@NonNull Set<BigInteger> primeBase)
	{
		addPrimeBases(BaseTypes.DEFAULT, primeBase, null);
	}

	@Override
	public void addPrimeBases(@NonNull Set<BigInteger> primeBase, @NonNull BaseTypes baseType)
	{
		addPrimeBases(baseType, primeBase, null);
	}

	/**
	 * For DEFAULT base type
	 */
	@Override
	public List<Set<BigInteger>> getPrimeBases()
	{
		return getPrimeBases(BaseTypes.DEFAULT);
	}

	@Override
	public List<Set<BigInteger>> getPrimeBases(@NonNull BaseTypes baseType)
	{
		return primeBases.getOrDefault(baseType, Collections.emptyList());
	}
}
