package com.starcases.prime.cache.impl;

import java.nio.file.Path;
import javax.cache.Cache;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import com.starcases.prime.cache.api.CacheProviderIntfc;
import lombok.NonNull;

/**
 * Provide API for cache handling.
 * @author scott
 *
 */
public class CacheProvider implements CacheProviderIntfc
{
	/**
	 * default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("CACHE");

	/**
	 * create target service.
	 *
	 * @param <K>
	 * @param <V>
	 * @param cacheName
	 * @param clearCache Remove all existing entries (at startup after persisted entries were reloaded).
	 * @return
	 */
	@Override
	public <K,V> Cache<K,V> create( @NonNull final Path cache, final boolean clearCache)
	{
		return new PrimeCacheImpl<>(cache.getFileName().toString(), cache.getParent(), clearCache);
	}

	/**
	 * get provider attributes.
	 */
	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
