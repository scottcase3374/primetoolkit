package com.starcases.prime.core.impl;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.core.api.CollectionTrackerIntfc;
import com.starcases.prime.core.api.CollectionTrackerProviderIntfc;

public class CollectionTrackerProvider implements CollectionTrackerProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("COLLECTION_TRACKER");

	@Override
	public CollectionTrackerIntfc create(final ImmutableMap<String,Object> settings)
	{
		return new CollectionTrackerImpl();
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
