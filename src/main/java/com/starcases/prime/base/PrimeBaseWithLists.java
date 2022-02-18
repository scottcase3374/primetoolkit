package com.starcases.prime.base;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
	private static PrimeSourceIntfc primeSrc;

	public static void setPrimeSource(@NonNull PrimeSourceIntfc primeSrcIntfc)
	{
		primeSrc = primeSrcIntfc;
	}

	/**
	 * Represents sets of base primes that sum to this Prime. (index to primes)
	 *
	 * NOTE: This supports ONE base per base type.
	 */
	@NonNull
	private final Map<BaseTypes, List<Integer>> primeBaseIdxs = new EnumMap<>(BaseTypes.class);

	@Getter
	private BaseMetadataIntfc baseMetadata;

	public PrimeBaseWithLists()
	{
		// nothing to init here
	}

	@Override
	public void addPrimeBase(@NonNull BaseTypes baseType, @NonNull BitSet primeBase, BaseMetadataIntfc baseMetadata)
	{
		this.primeBaseIdxs.merge(baseType,
				primeBase.stream().boxed().toList(),
				(a,b) -> b );
		this.baseMetadata = baseMetadata;
	}

	/**
	 * Include a set of primes in the set of Prime bases for the current Prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBase(@NonNull BitSet primeBase)
	{
		addPrimeBase(BaseTypes.DEFAULT, primeBase, null);
	}

	@Override
	public void addPrimeBase(@NonNull BitSet primeBase, @NonNull BaseTypes baseType)
	{
		addPrimeBase(baseType, primeBase, null);
	}

	/**
	 * size for DEFAULT base type
	 */
	@Override
	public int getBaseSize()
	{
		 return primeBaseIdxs.get(BaseTypes.DEFAULT).size();
	}

	@Override
	public BigInteger getMaxPrimeBase()
	{
		return getMaxPrimeBase(BaseTypes.DEFAULT);
	}

	@Override
	public BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType)
	{
		return primeSrc
				.getPrime(primeBaseIdxs
						.get(baseType)
						.stream()
						.max((i1, i2) -> i1.compareTo(i2))
						.orElseThrow())
				.orElse(BigInteger.ZERO);
	}

	/**
	 * For DEFAULT base type
	 */
	@Override
	public List<BitSet> getPrimeBaseIdxs()
	{
		return getPrimeBaseIdxs(BaseTypes.DEFAULT);
	}

	@Override
	public List<BitSet> getPrimeBaseIdxs(@NonNull BaseTypes baseType) {
		final var b = new BitSet();
		primeBaseIdxs.getOrDefault(baseType, Collections.emptyList()).stream().forEach(b::set);
		return List.of(b);
	}
}
