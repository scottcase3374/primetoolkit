package com.starcases.prime.intfc;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import com.starcases.prime.base.BaseTypes;

import lombok.NonNull;

/**
 * Interface supports multiple implementations having different tradeoffs.
 *
 * Note that the methods without a BaseTypes argument use BaseTypes.DEFAULT.
 * Generally, the code that creates the initial primes uses these no-arg versions
 * and any "alternative base" creation code should always use the BaseTypes argument
 * version when creating additional base info.
 *
 * Use the no-arg version if you
 * also need to get data regarding the default version while generating new bases.
 *
 *
 */
public interface PrimeBaseIntfc extends Serializable
{
	void addPrimeBases(@NonNull BaseTypes baseType, @NonNull List<Set<BigInteger>> primeBase, @NonNull BaseMetadataIntfc metadata);

	void addPrimeBases(@NonNull List<Set<BigInteger>> primeBase);

	void addPrimeBases(@NonNull List<Set<BigInteger>> primeBase, @NonNull BaseTypes baseType);

	BaseMetadataIntfc getBaseMetadata();

	/**
	 *
	 * Not every use case needs multiple bases per Prime
	 *
	 *
	 * @return No-arg version; so this returns data for Bases.DEFAULT
	 */
	List<Set<BigInteger>> getPrimeBases();

	/**
	 * Not every use case needs multiple bases per Prime
	 * @return
	 */
	List<Set<BigInteger>> getPrimeBases(@NonNull BaseTypes baseType);
}
