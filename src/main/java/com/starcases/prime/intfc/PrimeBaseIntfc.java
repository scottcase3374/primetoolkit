package com.starcases.prime.intfc;

import java.io.Serializable;

import com.starcases.prime.base.BaseTypes;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.MutableList;

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
	 * Add Sets of primes (longs) and a base metadata container to the specified base.
	 * @param baseType
	 * @param primeBase
	 * @param metadata
	 */
	void addPrimeBases(@NonNull BaseTypes baseType, @NonNull MutableList<ImmutableLongCollection> primeBase, @NonNull BaseMetadataIntfc metadata);

	/**
	 * Add sets of primes to current base
	 * @param primeBase
	 */
	void addPrimeBases(@NonNull MutableList<ImmutableLongCollection> primeBase);

	/**
	 * Add sets of primes for specified base.
	 * @param primeBase
	 * @param baseType
	 */
	void addPrimeBases(@NonNull MutableList<ImmutableLongCollection> primeBase, @NonNull BaseTypes baseType);

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
	MutableList<ImmutableLongCollection> getPrimeBases();

	/**
	 * Not every use case needs multiple bases per Prime
	 * @return
	 */
	MutableList<ImmutableLongCollection> getPrimeBases(@NonNull BaseTypes baseType);
}
