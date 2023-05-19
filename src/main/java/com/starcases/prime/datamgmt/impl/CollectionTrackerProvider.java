package com.starcases.prime.datamgmt.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.datamgmt.api.CollectionTrackerProviderIntfc;

public class CollectionTrackerProvider implements CollectionTrackerProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("COLLECTION_TRACKER");

	@Override
	public CollectionTrackerIntfc create(final ImmutableMap<String,Object> settings)
	{
		return new CollectionTrackerImpl();
	}

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
