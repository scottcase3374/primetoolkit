package com.starcases.prime.cache.impl.subset;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.cache.api.subset.SubsetIntfc;
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
	public SubsetIntfc<Long> create(@NonNull long[] primes)
	{
		final Long [] wrapper = new Long[primes.length];
		for (int i=0; i < primes.length; i++)
		{
			wrapper[i] = primes[i];
		}
		return new Subset<Long>(Long.class, wrapper);
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
