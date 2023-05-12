package com.starcases.prime.cache.impl;

import java.io.IOException;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import com.starcases.prime.cache.api.CacheProviderIntfc;
import com.starcases.prime.logging.PTKLogger;

import lombok.NonNull;


/**
 * Provide API for cache handling.
 * @author scott
 *
 */
public class CacheProvider implements CacheProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("CACHE");

	/**
	 * manage any/all caching to files
	 */
	private static EmbeddedCacheManager embCacheMgr;

	static
	{
		try
		{
			embCacheMgr = new DefaultCacheManager("infinispan.xml");
			embCacheMgr.startCaches("primes");
		}
		catch (final IOException e)
		{
			PTKLogger.output("Error starting pre-prime cache: %s",e);
		}
	}

	/**
	 *
	 * @param <K>
	 * @param <V>
	 * @param cacheName
	 * @param clearCache Remove all existing entries (at startup after persisted entries were reloaded).
	 * @return
	 */
	@Override
	public <K,V> Cache<K,V> create( @NonNull final String cacheName, final ImmutableMap<String,Object> settings)
	{
		return embCacheMgr.getCache(cacheName);
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
