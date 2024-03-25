package com.starcases.prime.cache.impl.prefixes;

import java.net.URI;
import java.nio.file.Path;
import java.util.Properties;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.impl.tuple.Tuples;

import com.starcases.prime.cache.api.CachePrefixProviderIntfc;
import com.starcases.prime.cache.api.PersistedCacheIntfc;
import com.starcases.prime.cache.api.PersistedPrefixCacheIntfc;

import lombok.NonNull;

/**
 * Provide API for cache handling.
 * @author scott
 *
 */
public class PrefixCacheProvider implements CachePrefixProviderIntfc
{
	/**
	 * default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PREFIX_CACHE_PROVIDER");

	private static final ConcurrentMutableMap<String, PersistedPrefixCacheIntfc> caches = new ConcurrentHashMap<>(10);

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
	public PersistedCacheIntfc create( @NonNull final Path cache, final boolean clearCache)
	{
		return null;
	}

	@Override
	public PersistedPrefixCacheIntfc create(@NonNull Path cache, boolean clearCache, int marker)
	{
		final PersistedPrefixCacheIntfc cacheImpl = new PrefixesSubsetCacheImpl(cache.getFileName().toString(), cache.getParent(), clearCache);
		caches.add(Tuples.pair( cache.getFileName().toString(), cacheImpl));
		return cacheImpl;
	}

	/**
	 * get provider attributes.
	 */
	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	//
	// Cache manager methods below
	//

	@Override
	public CachingProvider getCachingProvider()
	{

		return null;
	}

	@Override
	public URI getURI()
	{

		return null;
	}

	@Override
	public ClassLoader getClassLoader()
	{

		return null;
	}

	@Override
	public Properties getProperties()
	{

		return null;
	}

	@Override
	public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration)
			throws IllegalArgumentException
	{
		return null;
	}

	@Override
	public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType)
	{
		return null;
	}

	@Override
	public  PersistedPrefixCacheIntfc getCache(String cacheName)
	{
		return caches.get(cacheName);
	}

	@Override
	public Iterable<String> getCacheNames()
	{
		return caches.keySet();
	}

	@Override
	public void destroyCache(String cacheName)
	{


	}

	@Override
	public void enableManagement(String cacheName, boolean enabled)
	{


	}

	@Override
	public void enableStatistics(String cacheName, boolean enabled)
	{


	}

	@Override
	public void close()
	{
	}

	@Override
	public boolean isClosed()
	{
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> clazz)
	{
		return null;
	}

	@Override
	public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties)
	{
		return this;
	}

	@Override
	public ClassLoader getDefaultClassLoader()
	{
		return null;
	}

	@Override
	public URI getDefaultURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getDefaultProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CacheManager getCacheManager(URI uri, ClassLoader classLoader)
	{
		return this;
	}

	@Override
	public CacheManager getCacheManager()
	{
		return this;
	}

	@Override
	public void close(ClassLoader classLoader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close(URI uri, ClassLoader classLoader) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSupported(OptionalFeature optionalFeature) {
		// TODO Auto-generated method stub
		return false;
	}
}
