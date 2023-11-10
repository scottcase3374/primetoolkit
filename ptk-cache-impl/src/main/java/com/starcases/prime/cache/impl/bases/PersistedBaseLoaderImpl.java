package com.starcases.prime.cache.impl.bases;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.io.InputStream;

import javax.cache.Cache;

import com.beanit.asn1bean.ber.types.BerInteger;
import com.starcases.prime.cache.api.persistload.PersistLoaderIntfc;
import com.starcases.prime.cache.api.subset.SubsetIntfc;
import com.starcases.prime.cache.api.subset.BaseSubsetProviderIntfc;
import com.starcases.prime.cache.impl.BasesAsn;
import com.starcases.prime.cache.impl.BasesAsn.Primes;
import com.starcases.prime.cache.impl.CollBaseAsn;
import com.starcases.prime.cache.impl.CollPrimeAsn;

import lombok.NonNull;

/**
 * Load previously persisted prime data.
 */
public class PersistedBaseLoaderImpl implements PersistLoaderIntfc
{
	private static final Logger LOG = Logger.getLogger(PersistedBaseLoaderImpl.class.getName());

	private final Cache<Long, SubsetIntfc<Long[][]>> primeCache;
	private final BaseSubsetProviderIntfc subsetProvider;
	private final Path pathToCacheDir;

	public PersistedBaseLoaderImpl(@NonNull final Cache<Long, SubsetIntfc<Long[][]>> primeCache,
									@NonNull final Path pathToCacheDir,
									@NonNull final BaseSubsetProviderIntfc subsetProvider)
	{
		this.primeCache = primeCache;
		this.pathToCacheDir = pathToCacheDir;
		this.subsetProvider = subsetProvider;
	}

	@Override
	public boolean process()
	{
		return
			loadCache(

			(key, primes) ->
				{
					primeCache.put(key, subsetProvider.create(primes.length, primes));
					return 0;
				}
		);
	}

	private boolean loadCache(@NonNull final BiFunction<Long, long[][][], Integer> loader)
	{
		boolean ok = true;
		try (Stream<Path> stream = Files
				.list(pathToCacheDir)
				.parallel()
				.filter(f -> f.getFileName().toString().matches("\\d"))
				)
		{
		    stream.forEach(path ->
		    	{
				  final var basesAsn = new BasesAsn();

				  try (InputStream is = Files.newInputStream(path))
				  {
					  basesAsn.decode(is, true);

					  final String baseName = basesAsn.getBaseName().toString();
					  final int startIdx = basesAsn.getStartIndex().intValue();
					  final Primes primesAsn = basesAsn.getPrimes();

					  final List<CollBaseAsn> collBaseAsn = primesAsn.getCollBaseAsn();
					  final long[][][] primes = new long[collBaseAsn.size()][][];
					  for (int i=0; i< collBaseAsn.size(); i++)
					  {
						  final CollBaseAsn collBase = collBaseAsn.get(i);
						  final List<CollPrimeAsn> collPrimeAsn = collBase.getCollPrimeAsn();
						  primes[i] = new long[collPrimeAsn.size()][];
						  for (int j=0; j< collPrimeAsn.size(); j++)
						  {
							  final CollPrimeAsn collPrime = collPrimeAsn.get(j);
							  final List<BerInteger> primeAsn = collPrime.getBerInteger();
							  primes[i][j] = new long[primeAsn.size()];
							  for (int l=0; l<primeAsn.size(); l++)
							  {
								  primes[i][j][l] = primeAsn.get(l).longValue();
							  }
						  }
					  }

					  LOG.info(String.format("Loading Persisted Cache-file: %s", path.getFileName().toString()));
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
