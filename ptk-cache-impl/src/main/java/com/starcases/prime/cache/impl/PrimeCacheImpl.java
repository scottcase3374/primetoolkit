package com.starcases.prime.cache.impl;

import com.beanit.asn1bean.ber.ReverseByteArrayOutputStream;
import com.beanit.asn1bean.ber.types.BerInteger;
import com.starcases.prime.cache.api.subset.PrimeSubsetIntfc;
import com.starcases.prime.error.api.PtkErrorHandlerIntfc;
import com.starcases.prime.error.api.PtkErrorHandlerProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 * @param <K> Long
 * @param <V> PrimeSubsetIntfc
 */
public class PrimeCacheImpl<K,V> implements Cache<K,V>
{
	private final  PtkErrorHandlerIntfc errorHandler =
			new SvcLoader<PtkErrorHandlerProviderIntfc, Class<PtkErrorHandlerProviderIntfc>>(PtkErrorHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("ERROR_HANDLER")).orElseThrow().create();
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(PrimeCacheImpl.class.getName());

	private final Path pathToCacheDir;
	private final MutableMap<K,V> keysToValue = Maps.mutable.empty();

	/**
	 * constructor
	 *
	 * @param cacheName
	 * @param pathToCacheDirs
	 */
	public PrimeCacheImpl(@NonNull final String cacheName, @NonNull final Path pathToCacheDirs, final boolean clearCache)
	{
		try
		{
			if (LOG.isLoggable(Level.INFO))
			{
				LOG.info(String.format("Cache ctor : cacheName [%s] pathToCacheDirs: [%s]", cacheName, pathToCacheDirs.toString()));
			}

			final Path pathToCache = Path.of(pathToCacheDirs.toString(), cacheName).normalize();
			pathToCacheDir = Files.createDirectories(pathToCache);

			if (clearCache)
			{
				clearCache();
			}
		}
		catch(final IOException e)
		{
			throw new RuntimeException("can't create cache directory");
		}
	}

	private void clearCache()
	{
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(pathToCacheDir, "[0-9]*"))
		{
		    for (Path file: stream)
		    {
		    	Files.delete(file);
		    }
		} catch (IOException | DirectoryIteratorException x)
		{
		    System.err.println(x);
		}
	}

	@Override
	public V get(@NonNull final K key)
	{
		return keysToValue.get(key);
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

	public void persistAll()
	{
		LOG.info("Persisting Cached data");
		keysToValue.forEachKeyValue((k,v) -> persist(k,v));
	}

	  private void persist(@NonNull final K keyVal, @NonNull final V subsetVal)
	  {
		  if (subsetVal instanceof PrimeSubsetIntfc subset && keyVal instanceof Long key)
		  {
			  try
			  {
				  final var subsetAsn = new PrimeSubsetAsn();
				  final int count = (int)subset.getMaxOffsetAssigned();

				  final List<BerInteger> berInts =  new ArrayList<>(count);
				  for (long i : subset.getEntries())
				  {
					  berInts.add(new BerInteger(i));
				  }

				  var pr = subsetAsn.getBerInteger();
				  pr.addAll(berInts);

				  ReverseByteArrayOutputStream os = new ReverseByteArrayOutputStream(1_000_000, true);

				  int res = subsetAsn.encode(os, true);

				  try (	OutputStream oos = new FileOutputStream(Path.of(pathToCacheDir.toString(), key.toString()).toFile());
						InputStream is = new ByteArrayInputStream(os.getArray());)
				  {
					  is.transferTo(oos);
				  }

				  LOG.info(String.format("Persist batch: [%s] src-rec-count: [%d]", keyVal, count));
			  } catch(IOException e)
			  {
				  // Note that batches are not handled in numerical order when done using parallelism.
				  errorHandler.handleError(() -> "Problem persisting primes.", Level.SEVERE, e, false , true);
			  }
		  }
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
		return clazz.cast(this);
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