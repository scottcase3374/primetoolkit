package com.starcases.prime.intfc;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import com.starcases.prime.base.BaseTypes;

import lombok.NonNull;

/**
 * Interface supports multiple implementations
 * having different tradeoffs.
 *
 * Note that the methods without a BaseTypes argument
 * use BaseTypes.DEFAULT.
 *
 * Generally, the code that creates the initial primes
 * uses these no-arg versions
 * and any "alternative base" creation code should always
 * use the BaseTypes argument
 * version when creating additional base info.
 *
 * Use the no-arg version if you
 * also need to get data regarding the default version
 *  while generating new bases.
 *
 *
 */
@SuppressWarnings("PMD.CommentSize")
public interface PrimeBaseIntfc extends Serializable
{
	/**
	 * Add Sets of primes (big integers) and a base metadata container to the specified base.
	 * FIXME distinguish between this and the other similar methods.
	 * @param baseType
	 * @param primeBase
	 * @param metadata
	 */
	void addPrimeBases(@NonNull BaseTypes baseType, @NonNull List<Set<BigInteger>> primeBase, @NonNull BaseMetadataIntfc metadata);

	/**
	 * Add sets of primes to current base
	 * FIXME distinguish between this and the other similar methods.
	 * @param primeBase
	 */
	void addPrimeBases(@NonNull List<Set<BigInteger>> primeBase);

	/**
	 * Add sets of primes for specified base.
	 * FIXME distinguish between this and the other similar methods.
	 * @param primeBase
	 * @param baseType
	 */
	void addPrimeBases(@NonNull List<Set<BigInteger>> primeBase, @NonNull BaseTypes baseType);

	/**
	 * Get the base meta data container for the specified base type.
	 * @param baseType
	 * @return
	 */
	BaseMetadataIntfc getBaseMetadata(@NonNull BaseTypes baseType);

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
