package com.starcases.prime.cache.impl.subset;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.cache.api.subset.PrimeSubsetIntfc;
import com.starcases.prime.cache.api.subset.PrimeSubsetProviderIntfc;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class PrimeSubsetProvider implements PrimeSubsetProviderIntfc
{
	/**
	 * default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRIMESUBSET");

	/**
	 * create target service.
	 */
	@Override
	public PrimeSubsetIntfc create(@NonNull Integer count, @NonNull long[] primes)
	{
		return new PrimeSubset(count, primes);
	}

	/**
	 * get provider attributes.
	 */
	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
