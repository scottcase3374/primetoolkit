package com.starcases.prime.intfc;

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
public interface PrimeBaseIntfc
{
	@NonNull
	static final Comparator<BigInteger> bigIntComp = (b1, b2) -> b1.compareTo(b2);

	//
	// Prime base info - no BaseTypes-argument methods - uses Bases.DEFAULT
	//

	/**
	 *
	 * Not every use case needs multiple bases per prefixPrime - only the
	 * list of bitsets implementation supports multiples.
	 *
	 *
	 * @return No-arg version; so this returns data for Bases.DEFAULT
	 */
	List<BitSet> getPrimeBaseIdxs();

	/**
	 * No-arg version; so this returns data for Bases.DEFAULT
	 * @return
	 */
	BigInteger getMinPrimeBase();

	/**
	 * No-arg version; so this returns data for Bases.DEFAULT
	 * @return
	 */
	BigInteger getMaxPrimeBase();

	/**
	 * No-arg version; so this returns data for Bases.DEFAULT
	 * @return
	 */
	int getBaseSize();

	/**
	 * No baseTypes arg version; so this returns data for Bases.DEFAULT
	 *
	 * @param primeBase
	 */
	void addPrimeBase(@NonNull BitSet primeBase);


	//
	// Prime base info - accesses data for specified BaseTypes argument.
	//

	/**
	 * Not every use case needs multiple bases per prefixPrime - only the
	 * list of bitsets implementation supports multiples.
	 * @return
	 */
	List<BitSet> getPrimeBaseIdxs(@NonNull BaseTypes baseType);

	BigInteger getMinPrimeBase(@NonNull BaseTypes baseType);
	BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType);
	int getBaseSize(@NonNull BaseTypes baseType);
	void addPrimeBase(@NonNull BitSet primeBase, @NonNull BaseTypes baseType);
	void addPrimeBase(@NonNull BaseTypes baseType, @NonNull BitSet primeBase, @NonNull BaseMetadataIntfc metadata);

	BaseMetadataIntfc getBaseMetadata();
}
