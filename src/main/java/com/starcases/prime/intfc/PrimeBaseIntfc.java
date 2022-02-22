package com.starcases.prime.intfc;

import java.io.Serializable;
import java.math.BigInteger;
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


	void addPrimeBase(@NonNull BaseTypes baseType, @NonNull List<Integer> primeBase, @NonNull BaseMetadataIntfc metadata);

	/**
	 * No baseTypes arg version; so this returns data for Bases.DEFAULT
	 *
	 * @param primeBase
	 */
	void addPrimeBase(@NonNull List<Integer> primeBase);

	void addPrimeBase(@NonNull List<Integer> primeBase, @NonNull BaseTypes baseType);

	BaseMetadataIntfc getBaseMetadata();

	/**
	 *
	 * Not every use case needs multiple bases per Prime
	 *
	 *
	 * @return No-arg version; so this returns data for Bases.DEFAULT
	 */
	List<List<Integer>> getPrimeBaseIdxs();

	/**
	 * Not every use case needs multiple bases per Prime
	 * @return
	 */
	List<List<Integer>> getPrimeBaseIdxs(@NonNull BaseTypes baseType);
}
