package com.starcases.prime.base.api;

import jakarta.validation.constraints.NotNull;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.MutableList;

import com.starcases.prime.kern.api.BaseTypesIntfc;

/**
 * Interface supports multiple implementations
 * having different tradeoffs.
 *
 * Note that the methods without a BaseTypesIntfc argument
 * use BaseTypes.DEFAULT.
 *
 * Generally, the code that creates the initial primes
 * uses these no-arg versions
 * and any "alternative base" creation code should always
 * use the BaseTypesIntfc argument
 * version when creating additional base info.
 *
 * Use the no-arg version if you
 * also need to get data regarding the default version
 *  while generating new bases.
 *
 *
 */
public interface PrimeBaseIntfc
{
	/**
	 * Add Sets of primes (longs) and a base metadata container to the specified base.
	 * @param baseType
	 * @param primeBase
	 * @param metadata
	 */
	void addPrimeBases(long primeIdx, @NotNull BaseTypesIntfc baseType, @NotNull MutableList<ImmutableLongCollection> primeBase, @NotNull BaseMetadataIntfc metadata);

	/**
	 * Add sets of primes to current base
	 * @param primeBase
	 */
	void addPrimeBases(long primeIdx, @NotNull MutableList<ImmutableLongCollection> primeBase);

	/**
	 * Add sets of primes for specified base.
	 * @param primeBase
	 * @param baseType
	 */
	void addPrimeBases(long primeIdx, @NotNull MutableList<ImmutableLongCollection> primeBase, @NotNull BaseTypesIntfc baseType);

	/**
	 * Add sets of primes for specified base.
	 * @param primeBase
	 * @param baseType
	 */
	void addPrimeBases(long primeIdx, @NotNull ImmutableLongCollection primeBase, @NotNull BaseTypesIntfc baseType);

	/**
	 * Add array of primes for specified base.
	 * @param primeBase
	 * @param baseType
	 */
	void addPrimeBases(long primeIdx, @NotNull long[] primeBase, @NotNull BaseTypesIntfc baseType);

	/**
	 * Get the base meta data container for the specified base type.
	 * @param baseType
	 * @return
	 */
	BaseMetadataIntfc getBaseMetadata(@NotNull BaseTypesIntfc baseType);

	/**
	 *
	 * Not every use case needs multiple bases per Prime
	 *
	 *
	 * @return No-arg version; so this returns data for BasesSvcLoader.DEFAULT
	 */
	MutableList<ImmutableLongCollection> getPrimeBases();

	/**
	 * Not every use case needs multiple bases per Prime
	 * @return
	 */
	MutableList<ImmutableLongCollection> getPrimeBases(@NotNull BaseTypesIntfc baseType);
}
