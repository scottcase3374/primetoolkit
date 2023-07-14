package com.starcases.prime.cache.impl;

import com.beanit.asn1bean.ber.ReverseByteArrayOutputStream;
import com.beanit.asn1bean.ber.types.BerInteger;
import com.starcases.prime.cache.api.PrimeSubsetCacheIntfc;
import com.starcases.prime.cache.api.subset.PrimeSubsetIntfc;
import com.starcases.prime.cache.impl.subset.PrimeSubset;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
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
public class PrimeSubsetCacheImpl<K> implements PrimeSubsetCacheIntfc<K>
{
	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(PrimeSubsetCacheImpl.class.getName());

	private final Path pathToCacheDir;

	private final MutableMap<K,PrimeSubsetIntfc> keysToValue = Maps.mutable.empty();

	private boolean closed = false;

	/**
	 * constructor
	 *
	 * @param cacheName
	 * @param pathToCacheDirs
	 */
	public PrimeSubsetCacheImpl(@NonNull final String cacheName, @NonNull final Path pathToCacheDirs, final boolean clearCache)
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
	public PrimeSubsetIntfc get(@NonNull final K key)
	{
		return keysToValue.get(key);
	}

	@Override
	public Map<K, PrimeSubsetIntfc> getAll(@NonNull final Set<? extends K> keys)
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
	public void put(@NonNull final K key, @NonNull final PrimeSubsetIntfc value)
	{
		keysToValue.put(key, value);
	}

	@Override
	public void persistAll()
	{
		LOG.info("Persisting Cached data");
		keysToValue.forEachKeyValue((k,v) -> persist(k,v));
	}

	@Override
	public void persist(@NonNull final K keyVal, @NonNull final PrimeSubsetIntfc subset)
	  {
		  if (subset instanceof PrimeSubset subsetVal && keyVal instanceof Long key)
		  {
			  try
			  {
				  final var subsetAsn = new PrimeSubsetAsn();
				  final int count = (int)subsetVal.getMaxOffsetAssigned();

				  final List<BerInteger> berInts =  new ArrayList<>(count);
				  for (long i : subsetVal.getEntries())
				  {
					  berInts.add(new BerInteger(i));
				  }

				  final var pr = subsetAsn.getBerInteger();
				  pr.addAll(berInts);

				  final ReverseByteArrayOutputStream os = new ReverseByteArrayOutputStream(1_000_000, true);

				  subsetAsn.encode(os, true);

				  try (	OutputStream oos = new FileOutputStream(Path.of(pathToCacheDir.toString(), key.toString()).toFile());
						InputStream is = new ByteArrayInputStream(os.getArray());)
				  {
					  is.transferTo(oos);
				  }

				  LOG.info(String.format("Persist batch: [%s] src-rec-count: [%d]", keyVal, count));
			  } catch(IOException e)
			  {
				  // Note that batches are not handled in numerical order when done using parallelism.
				  statusHandler.handleError(() -> "Problem persisting primes.", Level.SEVERE, e, false , true);
			  }
		  }
	}

	@Override
	public PrimeSubsetIntfc getAndPut(@NonNull final K key, @NonNull final PrimeSubsetIntfc value)
	{
		return null;
	}

	@Override
	public void putAll(@NonNull final Map<? extends K, ? extends PrimeSubsetIntfc> map)
	{
	}

	@Override
	public boolean putIfAbsent(@NonNull final K key, @NonNull final PrimeSubsetIntfc value)
	{
		return keysToValue.putIfAbsent(key, value) != null;
	}

	@Override
	public boolean remove(@NonNull final K key)
	{
		return keysToValue.removeIf((k,v) -> containsKey(key));
	}

	@Override
	public boolean remove(@NonNull final K key, @NonNull final PrimeSubsetIntfc oldValue)
	{
		return keysToValue.remove(key, oldValue);
	}

	@Override
	public PrimeSubsetIntfc getAndRemove(@NonNull final K key)
	{
		// no-op
		return null;
	}

	@Override
	public boolean replace(@NonNull final K key, @NonNull final PrimeSubsetIntfc oldValue, @NonNull final PrimeSubsetIntfc newValue)
	{
		// no-op
		return false;
	}

	@Override
	public boolean replace(@NonNull final K key, @NonNull final PrimeSubsetIntfc value)
	{
		// no-op
		return false;
	}

	@Override
	public PrimeSubsetIntfc getAndReplace(@NonNull final K key, @NonNull final PrimeSubsetIntfc value)
	{
		// no-op
		return null;
	}

	@Override
	public void removeAll(@NonNull final Set<? extends K> keys)
	{
		// no-op
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
	public <C extends Configuration<K, PrimeSubsetIntfc>> C getConfiguration(@NonNull final Class<C> clazz)
	{
		return null;
	}

	@Override
	public <T> T invoke(@NonNull final K key, @NonNull final EntryProcessor<K, PrimeSubsetIntfc, T> entryProcessor, @NonNull final Object... arguments)
			throws EntryProcessorException
	{
		return null;
	}

	@Override
	public <T> Map<K, EntryProcessorResult<T>> invokeAll(@NonNull final Set<? extends K> keys, @NonNull final EntryProcessor<K, PrimeSubsetIntfc, T> entryProcessor,
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
		closed = true;
	}

	@Override
	public boolean isClosed()
	{
		return closed;
	}

	@Override
	public <T> T unwrap(@NonNull final Class<T> clazz)
	{
		return clazz.cast(this);
	}

	@Override
	public void registerCacheEntryListener(@NonNull final CacheEntryListenerConfiguration<K, PrimeSubsetIntfc> cacheEntryListenerConfiguration)
	{
	}

	@Override
	public void deregisterCacheEntryListener(@NonNull final CacheEntryListenerConfiguration<K, PrimeSubsetIntfc> cacheEntryListenerConfiguration)
	{
	}

	@Override
	public Iterator<Entry<K, PrimeSubsetIntfc>> iterator()
	{
		// no-op
		return null;
	}

	/*
	 * @Override public long getIfAbsent(long idx, long def) {
	 *
	 * return def; }
	 *
	 * @Override public long reduceIfEmpty(@NonNull LongLongToLongFunction fn, long
	 * primeDefault) { // TODO Auto-generated method stub return 0; }
	 *
	 * @Override public long reduceIfEmpty(@NonNull LongLongToLongFunction fn) { //
	 * TODO Auto-generated method stub return keysToValue.reduceIfEmpty(fn); }
	 *
	 * @Override public MutableLongLongMap select(@NonNull LongLongPredicate
	 * keyValPred) {
	 *
	 * // TODO Auto-generated method stub return null; }
	 *
	 *
	 * @Override public <R> R select(@NonNull LongPredicate pred, @NonNull R target)
	 * { // TODO Auto-generated method stub return null; }
	 *
	 * @Override public RichIterable<RichIterable<PrimeSubsetIntfc>> chunk(int size)
	 * { // TODO Auto-generated method stub return null; }
	 *
	 * @Override public <R> MutableBag<R> collectIf( Predicate<? super
	 * com.starcases.prime.cache.api.subset.PrimeSubsetIntfc> predicate, Function<?
	 * super com.starcases.prime.cache.api.subset.PrimeSubsetIntfc, ? extends R>
	 * function) { // TODO Auto-generated method stub return null; }
	 *
	 * @Override public MutableLongBag collectLong( LongFunction<? super
	 * com.starcases.prime.cache.api.subset.PrimeSubsetIntfc> longFunction) { //
	 * TODO Auto-generated method stub return null; }
	 *
	 * @Override public MapIterable<K, PrimeSubsetIntfc> ifPresentApply(K key,
	 * Function<? super com.starcases.prime.cache.api.subset.PrimeSubsetIntfc, ?
	 * extends Object> function) { // TODO Auto-generated method stub return null; }
	 */

}