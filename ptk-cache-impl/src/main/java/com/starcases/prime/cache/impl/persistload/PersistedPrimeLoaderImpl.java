package com.starcases.prime.cache.impl.persistload;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.io.InputStream;

import javax.cache.Cache;

import com.starcases.prime.cache.api.persistload.PersistLoaderIntfc;
import com.starcases.prime.cache.api.subset.PrimeSubsetIntfc;
import com.starcases.prime.cache.api.subset.PrimeSubsetProviderIntfc;
import com.starcases.prime.cache.impl.PrimeSubsetAsn;

import lombok.NonNull;

/**
 * Load previously persisted prime data.
 */
public class PersistedPrimeLoaderImpl implements PersistLoaderIntfc
{
	private static final Logger LOG = Logger.getLogger(PersistedPrimeLoaderImpl.class.getName());

	private final Cache<Long, PrimeSubsetIntfc> primeCache;
	private final PrimeSubsetProviderIntfc subsetProvider;
	private final Path pathToCacheDir;

	public PersistedPrimeLoaderImpl(@NonNull final Cache<Long, PrimeSubsetIntfc> primeCache,
									@NonNull final Path pathToCacheDir,
									@NonNull final PrimeSubsetProviderIntfc subsetProvider)
	{
		this.primeCache = primeCache;
		this.pathToCacheDir = pathToCacheDir;
		this.subsetProvider = subsetProvider;
	}


	public boolean process()
	{
		return loadCache(

				(key, primes) ->
					{
						primeCache.put(key, subsetProvider.create(primes.length, primes));
						return 0;
					}
			);
	}

	private boolean loadCache(@NonNull BiFunction<Long, long[], Integer> loader)
	{
		boolean ok = true;
		try (Stream<Path> stream = Files
				.list(pathToCacheDir)
				.filter(f -> f.getFileName().toString().matches("[0-9]+"))
				.sorted((x,y) -> Integer.valueOf(x.getFileName().toString()).compareTo(Integer.valueOf(y.getFileName().toString())));)
		{
		    stream.forEach(path ->
		    	{
				  final var subsetAsn = new PrimeSubsetAsn();

				  try (InputStream is = Files.newInputStream(path))
				  {
					  subsetAsn.decode(is, true);
				  }
				  catch(final IOException e)
				  {
					  throw new RuntimeException(e);
				  }

				  final int count = subsetAsn.getBerInteger().size();
				  final long[] primes = new long[count];

				  for (int i=0; i<count; i++)
				  {
					  primes[i] = subsetAsn.getBerInteger().get(i).longValue();
				  }

				  LOG.info(String.format("Loading Persisted Cache-file: %s", path.getFileName().toString()));
				  loader.apply(Long.valueOf(path.getFileName().toString()), primes);
		    	});
		}
		catch (IOException | DirectoryIteratorException x)
		{
		    System.err.println(x);
		    ok = false;
		}

	    return ok;
	}
}
