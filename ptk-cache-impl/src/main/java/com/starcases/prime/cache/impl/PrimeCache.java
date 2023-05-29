package com.starcases.prime.cache.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;


import lombok.NonNull;

/**
 *
 * @author scott
 *
 * @param <K>
 * @param <V>
 */
public class PrimeCache<K,V> implements Cache<K,V>
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(PrimeCache.class.getName());

	private static final FileAttribute<Set<PosixFilePermission>> fileAttrs = PosixFilePermissions
			.asFileAttribute(Set.of(new PosixFilePermission []{
											PosixFilePermission.OWNER_WRITE,
											PosixFilePermission.OWNER_READ}));

	private final Path pathToCacheDir;
	private final MutableMap<K,V> keysToValue = Maps.mutable.empty();
	private final BiFunction<K, Path, V> load;

	/**
	 * constructor
	 *
	 * @param cacheName
	 * @param pathToCacheDirs
	 */
	public PrimeCache(@NonNull final String cacheName, @NonNull final Path pathToCacheDirs, @NonNull final BiFunction<K, Path, V> load)
	{
		this.load = load;
		try
		{
			if (LOG.isLoggable(Level.INFO))
			{
				LOG.info(String.format("Cache ctor : cacheName [%s] pathToCacheDirs: [%s]", cacheName, pathToCacheDirs.toString()));
			}

			final Path pathToCache = Path.of(pathToCacheDirs.toString(), cacheName).normalize();
			pathToCacheDir = Files.createDirectories(pathToCache);

		}
		catch(final IOException e)
		{
			throw new RuntimeException("can't create cache directory");
		}
	}

	@Override
	public V get(@NonNull final K key)
	{
		return keysToValue.getIfAbsentPut(key, () -> load != null ? load.apply(key, pathToCacheDir) : null);
	}

	@Override
	public Map<K, V> getAll(@NonNull final Set<? extends K> keys)
	{
		return null;
	}

	@Override
	public boolean containsKey(@NonNull final K key)
	{
		return keysToValue.containsKey(key);
	}

	@Override
	public void loadAll(@NonNull final Set<? extends K> keys, boolean replaceExistingValues, @NonNull final CompletionListener completionListener)
	{
	}

	@Override
	public void put(@NonNull final K key, @NonNull final V value)
	{
		keysToValue.put(key, value);
	}

	@Override
	public V getAndPut(@NonNull final K key, @NonNull final V value)
	{
		return null;
	}

	@Override
	public void putAll(@NonNull final Map<? extends K, ? extends V> map)
	{
	}

	@Override
	public boolean putIfAbsent(@NonNull final K key, @NonNull final V value)
	{
		return false;
	}

	@Override
	public boolean remove(@NonNull final K key)
	{
		return keysToValue.removeIf((k,v) -> containsKey(key));
	}

	@Override
	public boolean remove(@NonNull final K key, @NonNull final V oldValue)
	{
		return keysToValue.remove(key, oldValue);
	}

	@Override
	public V getAndRemove(@NonNull final K key)
	{
		return null;
	}

	@Override
	public boolean replace(@NonNull final K key, @NonNull final V oldValue, @NonNull final V newValue)
	{
		return false;
	}

	@Override
	public boolean replace(@NonNull final K key, @NonNull final V value)
	{
		return false;
	}

	@Override
	public V getAndReplace(@NonNull final K key, @NonNull final V value)
	{
		return null;
	}

	@Override
	public void removeAll(@NonNull final Set<? extends K> keys)
	{
	}

	@Override
	public void removeAll()
	{
		clear();
	}

	@Override
	public void clear()
	{
		keysToValue.clear();
	}

	@Override
	public <C extends Configuration<K, V>> C getConfiguration(@NonNull final Class<C> clazz)
	{
		return null;
	}

	@Override
	public <T> T invoke(@NonNull final K key, @NonNull final EntryProcessor<K, V, T> entryProcessor, @NonNull final Object... arguments)
			throws EntryProcessorException
	{
		return null;
	}

	@Override
	public <T> Map<K, EntryProcessorResult<T>> invokeAll(@NonNull final Set<? extends K> keys, @NonNull final EntryProcessor<K, V, T> entryProcessor,
			@NonNull final Object... arguments)
	{
		return null;
	}

	@Override
	public String getName()
	{
		return pathToCacheDir.getFileName().toString();
	}

	@Override
	public CacheManager getCacheManager()
	{
		return null;
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
	public <T> T unwrap(@NonNull final Class<T> clazz)
	{
		return null;
	}

	@Override
	public void registerCacheEntryListener(@NonNull final CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration)
	{
	}

	@Override
	public void deregisterCacheEntryListener(@NonNull final CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration)
	{
	}

	@Override
	public Iterator<Entry<K, V>> iterator()
	{
		return null;
	}
}