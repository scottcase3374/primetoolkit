package com.starcases.prime.cache.impl.subset;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.cache.api.subset.PrefixSubsetIntfc;
import com.starcases.prime.cache.api.subset.PrefixSubsetProviderIntfc;
import com.starcases.prime.kern.api.Arrays;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class PrefixesSubsetProvider implements PrefixSubsetProviderIntfc
{
	/**
	 * default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PREFIX_SUBSET");

	/**
	 * create target service.
	 */
	@Override
	public PrefixSubsetIntfc create(@NonNull final long[][] bases)
	{
		final Long [][] wrapper = new Long[bases.length][];
		for (int i=0; i < bases.length; i++)
		{
			wrapper[i] = Arrays.longArrayToLongArray(bases[i]);
		}

		return new PrefixesSubset( wrapper);
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
