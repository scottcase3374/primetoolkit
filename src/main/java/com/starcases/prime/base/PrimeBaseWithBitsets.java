package com.starcases.prime.base;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.starcases.prime.intfc.BaseMetadataIntfc;
import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.Getter;
import lombok.NonNull;

public class PrimeBaseWithBitsets implements PrimeBaseIntfc
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
	 * Represents sets of base primes that sum to this prefixPrime. (index to primes)
	 *
	 * NOTE: This supports multiple alternative bases per base type.
	 */
	@NonNull
	private final Map<BaseTypes, List<BitSet>> primeBaseIdxs = new EnumMap<>(BaseTypes.class);

	@Getter
	private BaseMetadataIntfc baseMetadata;

	public PrimeBaseWithBitsets()
	{
		// nothing to init here
	}

	@Override
	public void addPrimeBase(@NonNull BaseTypes baseType, @NonNull BitSet primeBase, BaseMetadataIntfc baseMetadata)
	{
		this.primeBaseIdxs.compute(baseType,
				(k, v) ->
					{
						if (v == null)
						{
							return new ArrayList<BitSet>(Arrays.asList(primeBase));
						}

						v.add(primeBase);
						return v;
					});
		this.baseMetadata = baseMetadata;
	}

	/**
	 * Include a set of primes in the set of prefixPrime bases for the current prefixPrime.
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

	/**
	 * Need to think about how to handle multiple sets of bases for a single prefixPrime.  In that
	 * scenario, which base set should be used to determine the max prefixPrime base.  The
	 * current usage is just general reporting but the results should be consistent.
	 */
	@Override
	public BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType)
	{
		return primeBaseIdxs
				.get(baseType)
				.stream()
				.map(bs -> primeSrc
							.getPrime(bs.nextSetBit(0))
							.orElseThrow())
				.findAny()
				.orElseThrow();
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
	public List<BitSet> getPrimeBaseIdxs(@NonNull BaseTypes baseType)
	{
		return primeBaseIdxs.get(baseType);
	}
}
