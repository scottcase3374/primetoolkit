package com.starcases.prime.preload.impl;

import java.nio.file.Path;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.infinispan.Cache;

import com.starcases.prime.preload.api.PreloaderIntfc;
import com.starcases.prime.preload.api.PreloaderProviderIntfc;

public class PreloaderProvider implements PreloaderProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRELOADER");

	@Override
	public <K,V> PreloaderIntfc create(final Cache<Long, PrimeSubset> cache, final Path path, final ImmutableMap<String,Object> settings)
	{
		return new PrimeLoader(cache, path);
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
