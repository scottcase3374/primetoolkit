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
	// Represents sets of base primes that sum to this prefixPrime. (index to primes)
	private final Map<BaseTypes, List<Integer>> primeBaseIdxs = new EnumMap<>(BaseTypes.class);

	@Getter
	private BaseMetadataIntfc baseMetadata;

	@NonNull
	private static PrimeSourceIntfc primeSrc;

	public static void setPrimeSource(@NonNull PrimeSourceIntfc primeSrcIntfc)
	{
		primeSrc = primeSrcIntfc;
	}

	public PrimeBaseWithLists()
	{
		// nothing to init here
	}

	/**
	 * Returns the number of bases used to sum to the
	 * current prefixPrime.
	 */
	@Override
	public int getBaseSize()
	{
		 return primeBaseIdxs.get(BaseTypes.DEFAULT).size();
	}

	/**
	 * Returns the number of bases used to sum to the
	 * current prefixPrime.
	 */
	@Override
	public int getBaseSize(@NonNull BaseTypes baseType)
	{
		 return primeBaseIdxs.get(baseType).size();
	}

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

	/**
	 * Include a set of primes in the set of prefixPrime bases for the current prefixPrime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBase(@NonNull BitSet primeBase)
	{
		this.primeBaseIdxs
			.merge(
					BaseTypes.DEFAULT,
					primeBase.stream().boxed().toList(),
					(a,b) -> { a.addAll(b); return a; } );
	}

	@Override
	public void addPrimeBase(@NonNull BitSet primeBase, @NonNull BaseTypes baseType)
	{
		this.primeBaseIdxs.merge(baseType,
				primeBase.stream().boxed().toList(),
				(a,b) -> b );
	}

	@Override
	public void addPrimeBase(@NonNull BaseTypes baseType, @NonNull BitSet primeBase, @NonNull BaseMetadataIntfc baseMetadata)
	{
		this.primeBaseIdxs.merge(baseType,
				primeBase.stream().boxed().toList(),
				(a,b) -> b );
		this.baseMetadata = baseMetadata;
	}

	@Override
	public BigInteger getMinPrimeBase()
	{
		return getMinPrimeBase(BaseTypes.DEFAULT);
	}

	@Override
	public BigInteger getMaxPrimeBase()
	{
		return getMaxPrimeBase(BaseTypes.DEFAULT);
	}

	@Override
	public BigInteger getMinPrimeBase(@NonNull BaseTypes baseType)
	{
		return primeSrc.getPrime(primeBaseIdxs.get(baseType).stream().min((i1,i2) -> i1.compareTo(i2)).orElseThrow()).orElse(BigInteger.ZERO);
	}

	@Override
	public BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType)
	{
		return primeSrc.getPrime(primeBaseIdxs.get(baseType).stream().max((i1, i2) -> i1.compareTo(i2)).orElseThrow()).orElse(BigInteger.ZERO);
	}
}
