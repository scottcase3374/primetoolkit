package com.starcases.prime.cache.impl.prime;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.io.InputStream;
import java.io.BufferedInputStream;

import javax.cache.Cache;

import com.starcases.prime.cache.api.persistload.PersistLoaderIntfc;
import com.starcases.prime.cache.api.subset.SubsetIntfc;
import com.starcases.prime.cache.api.subset.PrimeSubsetProviderIntfc;
import com.starcases.prime.cache.impl.PrimeSubsetAsn;
import com.starcases.prime.kern.api.PtkException;

import lombok.NonNull;

/**
 * Load previously persisted prime data.
 */
public class PersistedPrimeLoaderImpl implements PersistLoaderIntfc
{
	private static final Logger LOG = Logger.getLogger(PersistedPrimeLoaderImpl.class.getName());

	private final Cache<Long, SubsetIntfc<Long>> primeCache;
	private final PrimeSubsetProviderIntfc subsetProvider;
	private final Path pathToCacheDir;

	public PersistedPrimeLoaderImpl(@NonNull final Cache<Long, SubsetIntfc<Long>> primeCache,
									@NonNull final Path pathToCacheDir,
									@NonNull final PrimeSubsetProviderIntfc subsetProvider)
	{
		this.primeCache = primeCache;
		this.pathToCacheDir = pathToCacheDir;
		this.subsetProvider = subsetProvider;
	}

	@Override
	public boolean process()
	{
		return loadCache(

				(key, primes) ->
					{
						primeCache.put(key, subsetProvider.create(primes));
						return 0;
					}
			);
	}

	private boolean loadCache(@NonNull final BiFunction<Long, long[], Integer> loader)
	{
		boolean ok = true;
		try (Stream<Path> stream = Files
				.list(pathToCacheDir)
				.parallel()
				.filter(f -> f.getFileName().toString().matches("\\d+"))
				)
		{
		    stream.forEach(path ->
		    	{
				  final var subsetAsn = new PrimeSubsetAsn();

				  try (InputStream is = new BufferedInputStream(Files.newInputStream(path)))
				  {
					  subsetAsn.decode(is, true);

					  final int count = subsetAsn.getBerInteger().size();
					  final long[] primes = new long[count];

					  for (int i=0; i<count; i++)
					  {
						  primes[i] = subsetAsn.getBerInteger().get(i).longValue();
					  }

					  LOG.info(String.format("Loading Persisted Cache-file: %s", path.getFileName().toString()));
					  loader.apply(Long.valueOf(path.getFileName().toString()), primes);
				  }
				  catch(final IOException e)
				  {
					  throw new PtkException(e);
				  }
		    	});
		}
		catch (IOException | DirectoryIteratorException x)
		{
		    LOG.severe(x.toString());
		    ok = false;
		}

	    return ok;
	}
}
