package com.starcases.prime.base.primetree.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.impl.PrimeBaseGenerator;

public class PrimeTreeProvider implements BaseProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRIME_TREE", "DEFAULT");

	/**
	 * collTrack setting is required.
	 */
	@Override
	public PrimeBaseGenerator create(final ImmutableMap<String,Object> settings)
	{
		final CollectionTrackerIntfc collTracker = (CollectionTrackerIntfc)settings.get("collTracker");

		return new PrimeTree(BaseTypes.PRIME_TREE, collTracker);
	}

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
