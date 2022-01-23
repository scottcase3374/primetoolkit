package com.starcases.prime.base;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.starcases.prime.intfc.BaseMetadataIntfc;
import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.Getter;
import lombok.NonNull;

public class PrimeBaseWithLists implements PrimeBaseIntfc
{
	@NonNull
	// Represents sets of base primes that sum to this prime. (index to primes)
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
	 * current prime.
	 */
	@Override
	public int getBaseSize()
	{
		 return primeBaseIdxs.get(BaseTypes.DEFAULT).size();
	}

	/**
	 * Returns the number of bases used to sum to the
	 * current prime.
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
		var b = new BitSet();
		primeBaseIdxs.getOrDefault(baseType, Collections.emptyList()).stream().forEach(b::set);
		return List.of(b);
	}

	/**
	 * Include a set of primes in the set of prime bases for the current prime.
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
		this.primeBaseIdxs.merge(baseType, primeBase.stream().boxed().toList(), (a,b) -> b );
	}

	@Override
	public void addPrimeBase(@NonNull BaseTypes baseType, @NonNull BitSet primeBase, @NonNull BaseMetadataIntfc baseMetadata)
	{
		this.primeBaseIdxs.merge(baseType, primeBase.stream().boxed().toList(), (a,b) -> b );
		this.baseMetadata = baseMetadata;
	}

	@Override
	public BigInteger getMinPrimeBase()
	{
		return primeBaseIdxs
				.get(BaseTypes.DEFAULT)
				.stream()
				.map(i -> primeSrc.getPrime(i))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.min(bigIntComp)
				.orElse(BigInteger.ZERO);
	}

	@Override
	public BigInteger getMaxPrimeBase()
	{
		return primeBaseIdxs.get(BaseTypes.DEFAULT).stream().map(i -> primeSrc.getPrime(i).get()).max(bigIntComp).get();
	}

	@Override
	public BigInteger getMinPrimeBase(@NonNull BaseTypes baseType)
	{
		return primeBaseIdxs.get(baseType).stream().map(i -> primeSrc.getPrime(i).get()).min(bigIntComp).get();
	}

	@Override
	public BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType)
	{
		return primeBaseIdxs
				.get(baseType)
				.stream()
				.map(i -> primeSrc.getPrime(i).orElse(BigInteger.ZERO))
				.max(bigIntComp)
				.orElse(BigInteger.ZERO);
	}
}
