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
public final class CacheMgr
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

	private CacheMgr()
	{}

	/**
	 *
	 * @param <K>
	 * @param <V>
	 * @param cacheName
	 * @param clearCache Remove all existing entries (at startup after persisted entries were reloaded).
	 * @return
	 */
	public static <K,V> Cache<K, V> getCache(final String cacheName, final boolean clearCache)
	{
		final Cache<K,V> cache = embCacheMgr.getCache(cacheName);
		if (clearCache)
		{
			cache.clear();
		}
		return cache;
	}

	/**
	 * Dump cache statistics
	 */
	public static void dumpStats(final String cacheName)
	{
		PrimeToolKit.output("Cache [%s] stats() : %s", new Object[] {cacheName, embCacheMgr.getCache(cacheName).getAdvancedCache().getStats().toJson()});
	}
}
