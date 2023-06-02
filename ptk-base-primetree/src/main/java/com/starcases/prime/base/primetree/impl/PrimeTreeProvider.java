package com.starcases.prime.base.primetree.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseGenFactoryIntfc;

/**
 *
 * @author scott
 *
 */
public class PrimeTreeProvider implements BaseProviderIntfc
{
	/**
	 * default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRIME_TREE", "DEFAULT");

	/**
	 * Create target service
	 *
	 * collTrack setting is required.
	 */
	@Override
	public BaseGenFactoryIntfc create(final ImmutableMap<String,Object> settings)
	{
		final CollectionTrackerIntfc collTracker = (CollectionTrackerIntfc)settings.get("collTracker");

		return new PrimeTree(PrimeTreeBaseType.PRIME_TREE, collTracker);
	}

	/**
	 * get provider attributes
	 */
	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
