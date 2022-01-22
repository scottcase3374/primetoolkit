package com.starcases.prime.base;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

public class PrimeBaseWithBitsets implements PrimeBaseIntfc
{
	public PrimeBaseWithBitsets()
	{}

	@NonNull
	private static PrimeSourceIntfc primeSrc;

	public static void setPrimeSource(@NonNull PrimeSourceIntfc primeSrcIntfc)
	{
		primeSrc = primeSrcIntfc;
	}

	/**
	 * Represents sets of base primes that sum to this prime. (index to primes)
	 *
	 * NOTE: This supports multiple alternative bases per base type - the PrimeBaseWithLists only supports 1 base
	 *       per base type.
	 */
	@NonNull
	private final Map<BaseTypes, List<BitSet>> primeBaseIdxs = new EnumMap<>(BaseTypes.class);

	/**
	 * size for DEFAULT base type
	 */
	@Override
	public int getBaseSize()
	{
		 return primeBaseIdxs.get(BaseTypes.DEFAULT).size();
	}

	/**
	 * size for DEFAULT base type
	 */
	@Override
	public int getBaseSize(@NonNull BaseTypes baseType)
	{
		 return primeBaseIdxs.get(baseType).size();
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

	/**
	 * Include a set of primes in the set of prime bases for the current prime.
	 * @param primeBase
	 */
	@Override
	public void addPrimeBase(@NonNull BitSet primeBase)
	{
		addPrimeBase(primeBase, BaseTypes.DEFAULT);
	}

	@Override
	public void addPrimeBase(@NonNull BitSet primeBase, @NonNull BaseTypes baseType)
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
	}

	@Override
	public BigInteger getMinPrimeBase()
	{
		return getMinPrimeBase(BaseTypes.DEFAULT);
	}

	/**
	 * Need to think about how to handle multiple sets of bases for a single prime.  In that
	 * scenario, which base set should be used to determine the min prime base.  The
	 * current usage is just general reporting but the results should be consistent.
	 */
	@Override
	public BigInteger getMinPrimeBase(@NonNull BaseTypes baseType)
	{
		// Need to redo this; the result of Optional<Optional<>> is just silly.
		return primeBaseIdxs
				.get(baseType)
				.stream()
				.map(bs -> bs.stream().boxed().map(i -> PrimeBaseWithBitsets.primeSrc.getPrimeRef(i).get().getPrime()).min(bigIntComp))
				.findAny().get().get();
	}

	@Override
	public BigInteger getMaxPrimeBase()
	{
		return getMaxPrimeBase(BaseTypes.DEFAULT);
	}

	/**
	 * Need to think about how to handle multiple sets of bases for a single prime.  In that
	 * scenario, which base set should be used to determine the max prime base.  The
	 * current usage is just general reporting but the results should be consistent.
	 */
	@Override
	public BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType)
	{
		// Need to redo this; the result of Optional<Optional<>> is just silly.
		return primeBaseIdxs
				.get(baseType)
				.stream()
				.map(bs -> bs.stream().boxed().map(i -> PrimeBaseWithBitsets.primeSrc.getPrimeRef(i).get().getPrime()).max(bigIntComp))
				.findAny().get().get();
	}

}
