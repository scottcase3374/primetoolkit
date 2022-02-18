package com.starcases.prime.intfc;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

import com.starcases.prime.base.BaseTypes;

import lombok.NonNull;

/**
 * Interface supports multiple implementations having different tradeoffs.
 *
 * Note that the methods without a BaseTypes argument use BaseTypes.DEFAULT.
 * Generally, the code that creates the initial primes uses these no-arg versions
 * and any "alternative base" creation code should always use the BaseTypes argument
 * version when creating the additional base info.
 *
 * Use the no-arg version if you
 * also need to get data regarding the default version while generating new bases.
 *
 *
 */
public interface PrimeBaseIntfc extends Serializable
{
	@NonNull
	static final Comparator<BigInteger> bigIntComp = (b1, b2) -> b1.compareTo(b2);


	void addPrimeBase(@NonNull BaseTypes baseType, @NonNull BitSet primeBase, @NonNull BaseMetadataIntfc metadata);

	/**
	 * No baseTypes arg version; so this returns data for Bases.DEFAULT
	 *
	 * @param primeBase
	 */
	void addPrimeBase(@NonNull BitSet primeBase);

	void addPrimeBase(@NonNull BitSet primeBase, @NonNull BaseTypes baseType);

	BaseMetadataIntfc getBaseMetadata();

	/**
	 * No-arg version; so this returns data for Bases.DEFAULT
	 * @return
	 */
	int getBaseSize();

	/**
	 * No-arg version; so this returns data for Bases.DEFAULT
	 * @return
	 */
	BigInteger getMaxPrimeBase();
	BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType);

	/**
	 *
	 * Not every use case needs multiple bases per Prime - only the
	 * list of bitsets implementation supports multiples.
	 *
	 *
	 * @return No-arg version; so this returns data for Bases.DEFAULT
	 */
	List<BitSet> getPrimeBaseIdxs();

	/**
	 * Not every use case needs multiple bases per Prime - only the
	 * list of bitsets implementation supports multiples.
	 * @return
	 */
	List<BitSet> getPrimeBaseIdxs(@NonNull BaseTypes baseType);
}
