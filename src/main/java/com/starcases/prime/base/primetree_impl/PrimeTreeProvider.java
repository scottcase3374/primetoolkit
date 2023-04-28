package com.starcases.prime.base.primetree_impl;

import java.util.Map;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.base_api.BaseProviderIntfc;
import com.starcases.prime.base_impl.AbstractPrimeBaseGenerator;
import com.starcases.prime.core_api.PrimeSourceIntfc;
import com.starcases.prime.core_api.CollectionTrackerIntfc;

public class PrimeTreeProvider implements BaseProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRIME_TREE");

	/**
	 * collTrack setting is required.
	 */
	@Override
	public AbstractPrimeBaseGenerator create(final PrimeSourceIntfc primeSrc, final Map<String, Object> settings)
	{
		final CollectionTrackerIntfc collTrack = (CollectionTrackerIntfc)settings.get("collTrack");

		return new PrimeTree(primeSrc, collTrack);
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
