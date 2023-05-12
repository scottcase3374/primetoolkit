package com.starcases.prime.base.primetree.impl;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.PrimeBaseGenerateIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;

import lombok.NonNull;

public class PrimeTreeProvider implements BaseProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRIME_TREE", "DEFAULT");

	/**
	 * collTrack setting is required.
	 */
	@Override
	public PrimeBaseGenerateIntfc create(@NonNull final PrimeSourceIntfc primeSrc, final ImmutableMap<String,Object> settings)
	{
		final CollectionTrackerIntfc collTracker = (CollectionTrackerIntfc)settings.get("collTracker");

		return new PrimeTree(primeSrc, collTracker);
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
