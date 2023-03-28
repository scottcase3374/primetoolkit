package com.starcases.prime.cache;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import com.starcases.prime.PrimeToolKit;


/**
 * Provide API for cache handling.
 * @author scott
 *
 */
public class CacheMgr
{
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
			PrimeToolKit.output("Error starting pre-prime cache: %s",e);
		}
	}

	public CacheMgr()
	{

	}

	/**
	 *
	 * @param <K>
	 * @param <V>
	 * @param name
	 * @param clearCache Remove all existing entries (at startup after persisted entries were reloaded).
	 * @return
	 */
	public <K,V> Cache<K, V> getCache(final String name, final boolean clearCache)
	{
		final Cache<K,V> cache = embCacheMgr.getCache(name);
		cache.clear();
		return cache;
	}
}
