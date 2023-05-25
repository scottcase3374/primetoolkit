package com.starcases.prime.cache.impl;

import java.nio.file.Path;

import javax.cache.Cache;

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
	 *
	 * @param <K>
	 * @param <V>
	 * @param cacheName
	 * @param clearCache Remove all existing entries (at startup after persisted entries were reloaded).
	 * @return
	 */
	@Override
	public <K,V> Cache<K,V> create( @NonNull final Path cache, final ImmutableMap<String,Object> settings)
	{
		return new PrimeCache<K,V>(cache.getFileName().toString(), cache.getParent());
	}

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
