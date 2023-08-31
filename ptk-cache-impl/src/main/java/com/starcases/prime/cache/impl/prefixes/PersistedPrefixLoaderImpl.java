package com.starcases.prime.cache.impl.prefixes;

import java.io.BufferedInputStream;

import java.io.IOException;
import java.io.InputStream;

import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.cache.Cache;

import com.starcases.prime.cache.api.persistload.PersistLoaderIntfc;
import com.starcases.prime.cache.api.subset.PrefixSubsetProviderIntfc;
import com.starcases.prime.cache.api.subset.SubsetIntfc;
import com.starcases.prime.cache.impl.CollPrimeAsn;
import com.starcases.prime.cache.impl.PrefixAsn;

import lombok.NonNull;

/**
 * manage prefix lists
 * sites.
 */
class PersistedPrefixLoaderImpl implements PersistLoaderIntfc
{
	private static final Logger LOG = Logger.getLogger(PersistedPrefixLoaderImpl.class.getName());

	private final Cache<Long, SubsetIntfc<Long[]>> prefixCache;
	private final PrefixSubsetProviderIntfc subsetProvider;
	private final Path pathToCacheDir;

	public PersistedPrefixLoaderImpl(@NonNull final Cache<Long, SubsetIntfc<Long[]>> prefixCache,
									@NonNull final Path pathToCacheDir,
									@NonNull final PrefixSubsetProviderIntfc subsetProvider)
	{
		this.prefixCache = prefixCache;
		this.pathToCacheDir = pathToCacheDir;
		this.subsetProvider = subsetProvider;
	}

	@Override
	public boolean process()
	{
		return loadCache(

				(key, primes) ->
					{
						prefixCache.put(key, subsetProvider.create(primes.length, primes));
						return 0;
					}
			);
	}

	private boolean loadCache(@NonNull final BiFunction<Long, long[][], Integer> loader)
	{
		boolean ok = true;
		try (Stream<Path> stream = Files
				.list(pathToCacheDir)
				.parallel()
				.filter(f -> f.getFileName().toString().matches("[0-9]+"))
				)
		{
		    stream.forEach(path ->
		    	{
				  final var prefixAsn = new PrefixAsn();

				  try (InputStream is = new BufferedInputStream(Files.newInputStream(path)))
				  {
					  prefixAsn.decode(is, true);

					  final List<CollPrimeAsn> collPrimeAsnColl = prefixAsn.getCollPrimeAsn();
					  final int count = collPrimeAsnColl.size();

					  final long [][] primes = new long[count][];
					  for (int i=0; i<count; i++)
					  {
						  final CollPrimeAsn collPrimeAsn = collPrimeAsnColl.get(i);

						  final int x = collPrimeAsn.getBerInteger().size();
						  primes[i] = new long[x];
						  for (int v=0; v < x; v++)
						  {
							  primes[i][v] = collPrimeAsn.getBerInteger().get(v).longValue();
						  }
					  }

					  LOG.info(String.format("Loading Persisted Prefix Cache-file: %s", path.getFileName().toString()));
					  loader.apply(Long.valueOf(path.getFileName().toString()), primes);
				  }
				  catch(final IOException e)
				  {
					  throw new RuntimeException(e);
				  }
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
