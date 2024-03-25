package com.starcases.prime.cache.impl.prefixes;

import com.beanit.asn1bean.ber.ReverseByteArrayOutputStream;
import com.beanit.asn1bean.ber.types.BerInteger;
import com.starcases.prime.cache.api.PersistedPrefixCacheIntfc;
import com.starcases.prime.cache.api.subset.PrefixSubsetIntfc;
import com.starcases.prime.cache.impl.BasesAsn;
import com.starcases.prime.cache.impl.CollBaseAsn;
import com.starcases.prime.cache.impl.CollPrimeAsn;
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

import lombok.Getter;
import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class PersistedPrefixCacheImpl implements PersistedPrefixCacheIntfc
{
	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(PersistedPrefixCacheImpl.class.getName());

	private final Path pathToCacheDir;

	/**
	 *  Map subset idx to subset of prefix arrays
	 */
	private final MutableMap<Long,PrefixSubsetIntfc> keysToValue = Maps.mutable.empty();

	private boolean closed = false;

	@Getter
	private long numCacheEntries;

	/**
	 * constructor
	 *
	 * @param cacheName
	 * @param pathToCacheDirs
	 */
	public PersistedPrefixCacheImpl(@NonNull final String cacheName, @NonNull final Path pathToCacheDirs, final boolean clearCache)
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
			LOG.severe(x.toString());
		}
	}

	@Override
	public PrefixSubsetIntfc get(@NonNull final Long key)
	{
		return keysToValue.get(key);
	}

	@Override
	public Map<Long, PrefixSubsetIntfc> getAll(@NonNull final Set<? extends Long> keys)
	{
		return Maps.mutable.empty();
	}

	@Override
	public boolean containsKey(@NonNull final Long key)
	{
		return keysToValue.containsKey(key);
	}

	@Override
	public void loadAll(@NonNull final Set<? extends Long> keys, boolean replaceExistingValues, @NonNull final CompletionListener completionListener)
	{
		/* ignored */
	}

	@Override
	public void put(@NonNull final Long key, @NonNull final PrefixSubsetIntfc value)
	{
		keysToValue.put(key, value);
		numCacheEntries++;
	}

	@Override
	public void persistAll()
	{
		LOG.info("Persisting ALL base data");
		keysToValue.forEachKeyValue(this::persist);
	}

	@Override
	public void persist(@NonNull final Long keyVal, @NonNull final PrefixSubsetIntfc subset)
	  {
		  try
		  {
			  final var basesAsn = new BasesAsn();
			  final var basePrimes = basesAsn.getPrimes();
			  final List<CollBaseAsn> collBaseAsn = basePrimes.getCollBaseAsn();

			  final Long [][] primes = subset.getEntries();
			  for (int i=0; i< primes.length; i++)
			  {
				  // for prefixes, each entry gets exactly 1 collBaseAsn
				  final CollBaseAsn collBase = new CollBaseAsn();
				  collBaseAsn.add(collBase);


				  final List<CollPrimeAsn> collPrimes = collBase.getCollPrimeAsn();
				  final CollPrimeAsn collPrimeAsn = new CollPrimeAsn();
				  collPrimes.add(collPrimeAsn);

				  for (int baseIdx=0; baseIdx< primes[i].length; baseIdx++)
				  {
					  final List<BerInteger> primeAsn = collPrimeAsn.getBerInteger();
					  primeAsn.add(new BerInteger(primes[i][baseIdx]));
				  }
			  }

			  final ReverseByteArrayOutputStream os = new ReverseByteArrayOutputStream(1_000_000, true);

			  basesAsn.encode(os, true);

			  try (	OutputStream oos = new FileOutputStream(Path.of(pathToCacheDir.toString(), keyVal.toString()).toFile());
					InputStream is = new ByteArrayInputStream(os.getArray());)
			  {
				  is.transferTo(oos);
			  }

			  if (LOG.isLoggable(Level.INFO))
			  {
				  LOG.info(String.format("PersistedPrefixCacheImpl::persist batch: [%s] src-rec-count: [%d]", keyVal, primes.length));
			  }
		  } catch(IOException e)
		  {
			  // Note that batches are not handled in numerical order when done using parallelism.
			  statusHandler.handleError(() -> "Problem persisting primes.", Level.SEVERE, e, false , true);
		  }
	}

	@Override
	public PrefixSubsetIntfc getAndPut(@NonNull final Long key, @NonNull final PrefixSubsetIntfc value)
	{
		return null;
	}

	@Override
	public void putAll(@NonNull final Map<? extends Long, ? extends PrefixSubsetIntfc> map)
	{
		/* ignored */
	}

	@Override
	public boolean putIfAbsent(@NonNull final Long key, @NonNull final PrefixSubsetIntfc value)
	{
		return keysToValue.putIfAbsent(key, value) != null;
	}

	@Override
	public boolean remove(@NonNull final Long key)
	{
		return keysToValue.removeIf((k,v) -> containsKey(key));
	}

	@Override
	public boolean remove(@NonNull final Long key, @NonNull final PrefixSubsetIntfc oldValue)
	{
		return keysToValue.remove(key, oldValue);
	}

	@Override
	public PrefixSubsetIntfc getAndRemove(@NonNull final Long key)
	{
		// no-op
		return null;
	}

	@Override
	public boolean replace(@NonNull final Long key, @NonNull final PrefixSubsetIntfc oldValue, @NonNull final PrefixSubsetIntfc newValue)
	{
		// no-op
		return false;
	}

	@Override
	public boolean replace(@NonNull final Long key, @NonNull final PrefixSubsetIntfc value)
	{
		// no-op
		return false;
	}

	@Override
	public PrefixSubsetIntfc getAndReplace(@NonNull final Long key, @NonNull final PrefixSubsetIntfc value)
	{
		// no-op
		return null;
	}

	@Override
	public void removeAll(@NonNull final Set<? extends Long> keys)
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
	public <C extends Configuration<Long, PrefixSubsetIntfc>> C getConfiguration(@NonNull final Class<C> clazz)
	{
		return null;
	}

	@Override
	public <T> T invoke(@NonNull final Long key, @NonNull final EntryProcessor<Long, PrefixSubsetIntfc, T> entryProcessor, @NonNull final Object... arguments)
			throws EntryProcessorException
	{
		return null;
	}

	@Override
	public <T> Map<Long, EntryProcessorResult<T>> invokeAll(@NonNull final Set<? extends Long> keys, @NonNull final EntryProcessor<Long, PrefixSubsetIntfc, T> entryProcessor,
			@NonNull final Object... arguments)
	{
		return Maps.mutable.empty();
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
	public void registerCacheEntryListener(@NonNull final CacheEntryListenerConfiguration<Long, PrefixSubsetIntfc> cacheEntryListenerConfiguration)
	{
		/* ignored */
	}

	@Override
	public void deregisterCacheEntryListener(@NonNull final CacheEntryListenerConfiguration<Long, PrefixSubsetIntfc> cacheEntryListenerConfiguration)
	{
		/* ignored */
	}

	@Override
	public Iterator<Entry<Long, PrefixSubsetIntfc>> iterator()
	{
		// no-op
		return null;
	}
}