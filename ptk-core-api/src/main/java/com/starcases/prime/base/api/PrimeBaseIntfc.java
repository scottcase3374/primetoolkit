package com.starcases.prime.base.api;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.MutableList;

import com.starcases.prime.kern.api.BaseTypesIntfc;

import lombok.NonNull;

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
	void addPrimeBases(long primeIdx, @NonNull BaseTypesIntfc baseType, @NonNull MutableList<ImmutableLongCollection> primeBase, @NonNull BaseMetadataIntfc metadata);

	/**
	 * Add sets of primes to current base
	 * @param primeBase
	 */
	void addPrimeBases(long primeIdx, @NonNull MutableList<ImmutableLongCollection> primeBase);

	/**
	 * Add sets of primes for specified base.
	 * @param primeBase
	 * @param baseType
	 */
	void addPrimeBases(long primeIdx, @NonNull MutableList<ImmutableLongCollection> primeBase, @NonNull BaseTypesIntfc baseType);

	/**
	 * Add sets of primes for specified base.
	 * @param primeBase
	 * @param baseType
	 */
	void addPrimeBases(long primeIdx, @NonNull ImmutableLongCollection primeBase, @NonNull BaseTypesIntfc baseType);

	/**
	 * Get the base meta data container for the specified base type.
	 * @param baseType
	 * @return
	 */
	BaseMetadataIntfc getBaseMetadata(@NonNull BaseTypesIntfc baseType);

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
	MutableList<ImmutableLongCollection> getPrimeBases(@NonNull BaseTypesIntfc baseType);
}
