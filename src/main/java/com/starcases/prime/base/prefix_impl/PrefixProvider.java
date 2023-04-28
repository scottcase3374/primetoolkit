package com.starcases.prime.base.prefix_impl;

import java.util.Map;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.base_api.BaseProviderIntfc;
import com.starcases.prime.base_impl.AbstractPrimeBaseGenerator;
import com.starcases.prime.core_api.PrimeSourceIntfc;

public class PrefixProvider implements BaseProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PREFIX");

	/**
	 *
	 */
	@Override
	public AbstractPrimeBaseGenerator create(final PrimeSourceIntfc primeSrc, final Map<String, Object> settings)
	{
		return new BasePrefixes(primeSrc);
	}

	@Override
	public int countAttributesMatch(final ImmutableCollection<String> attributes)
	{
		int ret = 0;
		if (ATTRIBUTES.containsAllIterable(attributes))
		{
			ret = 1;
		}
		return ret;
	}
}
