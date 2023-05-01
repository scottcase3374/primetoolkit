package com.starcases.prime.base.primetree.impl;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.impl.AbstractPrimeBaseGenerator;
import com.starcases.prime.core.api.CollectionTrackerIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;

public class PrimeTreeProvider implements BaseProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRIME_TREE", "DEFAULT");

	/**
	 * collTrack setting is required.
	 */
	@Override
	public AbstractPrimeBaseGenerator create(final PrimeSourceIntfc primeSrc, final ImmutableMap<String,Object> settings)
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
			ret = ATTRIBUTES.size();
		}
		return ret;
	}
}
