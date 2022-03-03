package com.starcases.prime.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.infinispan.protostream.annotations.ProtoFactory;

import com.starcases.prime.intfc.BaseMetadataIntfc;
import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.Getter;
import lombok.NonNull;

public class PrimeBaseWithLists implements PrimeBaseIntfc
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
	private final Map<BaseTypes, List<List<Integer>>> primeBaseIdxs = new EnumMap<>(BaseTypes.class);

	@Getter
	private BaseMetadataIntfc baseMetadata;

	@ProtoFactory
	public PrimeBaseWithLists()
	{
		// nothing to init here
	}

	@Override
	public void addPrimeBase(@NonNull BaseTypes baseType, @NonNull List<Integer> primeBase, BaseMetadataIntfc baseMetadata)
	{
		this.primeBaseIdxs.compute(baseType,
				(k, v) ->
					{
						if (v == null)
						{
							return new ArrayList<List<Integer>>(Arrays.asList(primeBase));
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
	public void addPrimeBase(@NonNull List<Integer> primeBase)
	{
		addPrimeBase(BaseTypes.DEFAULT, primeBase, null);
	}

	@Override
	public void addPrimeBase(@NonNull List<Integer> primeBase, @NonNull BaseTypes baseType)
	{
		addPrimeBase(baseType, primeBase, null);
	}

	/**
	 * For DEFAULT base type
	 */
	@Override
	public List<List<Integer>> getPrimeBaseIdxs()
	{
		return getPrimeBaseIdxs(BaseTypes.DEFAULT);
	}

	@Override
	public List<List<Integer>> getPrimeBaseIdxs(@NonNull BaseTypes baseType)
	{
		return primeBaseIdxs.getOrDefault(baseType, Collections.emptyList());
	}
}
