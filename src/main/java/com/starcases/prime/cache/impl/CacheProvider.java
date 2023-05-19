package com.starcases.prime.cache.impl;

import java.net.URI;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.cache.api.CacheProviderIntfc;

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
	private static final CacheManager CACHEMGR = Caching.getCachingProvider().getCacheManager(URI.create("infinispan.xml"), Thread.currentThread().getContextClassLoader());

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
		return CACHEMGR.getCache(cacheName);
	}

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
