package com.starcases.prime.cache.impl.preload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.OptionalLong;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import javax.cache.Cache;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.cache.api.preload.PreloaderIntfc;
import com.starcases.prime.cache.api.subset.PrimeSubsetIntfc;
import com.starcases.prime.cache.impl.subset.PrimeSubset;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.AccessLevel;

/**
 * manage primes which were pre-determined from another source.
 * Typically loaded from files sourced from various internet
 * sites.
 */
class PrimePreloaderImpl implements PreloaderIntfc
{
	Logger LOG = Logger.getLogger(PrimePreloaderImpl.class.getName());

	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
	/**
	 * Define the number of bits to batch together to reduce the number of
	 * idxToPrimeCache accesses.  This is to reduce the space overhead which is
	 * significant at large scale and to a lesser extent reduce time
	 * spent in idxToPrimeCache activities.
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final  int SUBSET_BITS = 17;

	/**
	 * Convert the number of bits into the size of an array for the
	 * caching.
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final int SUBSET_SIZE = 1 << SUBSET_BITS;

	/**
	 * represents max-assigned offset for a batch (subset).
	 */
	@Getter
	private long maxOffset = -1;

	/**
	 * Paths to source folders containing files of any pre-computed prime/base info.
	 */
	@Getter
	@Setter
	private Path [] sourceFolders;

	/**
	 * Container for the batches
	 */
	@Getter
	@Setter
	private PrimeSubset subsetOfIdxToPrime = new PrimeSubset();

	/**
	 * Cache object - both in-memory and persisted data
	 * 	Map index to prime#.
	 */
	@Getter(AccessLevel.PRIVATE)
	private final Cache<Long,PrimeSubsetIntfc> idxToPrimeCache;

	/**
	 * Break linear index range into indexed batches of indexed items.
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private long subsetIdx;

	private static final String ZIP_FOLDER_ISSUE_MSG = "Problem with input zip-file or folder";

	/**
	 * Constructor for class responsible for loading pre-generated prime/base info.
	 *
	 * @param sourceFolders
	 */
	public PrimePreloaderImpl(final Cache<Long, PrimeSubsetIntfc> idxToPrimeCache, final Path ... sourceFolders)
	{
		this.idxToPrimeCache = idxToPrimeCache;
		this.sourceFolders = sourceFolders.clone();
		subsetOfIdxToPrime.alloc(SUBSET_SIZE);

		/*
		 * BiFunction<Long, long[], Integer> loadCache = (key, primes) -> {
		 * idxToPrimeCache.put(key, generateSubset(primes.length, primes)); return 0; };
		 */

	}

	/**
	 * Centralized the mapping from requested idx to subset/offset values.
	 *
	 * @param idx
	 * @param retSubset
	 * @param retOffset
	 */
	private void convertIdxToSubsetAndOffset(final long idx, @NonNull final long [] retSubset, @NonNull final int [] retOffset)
	{
		final long subsetId = idx / SUBSET_SIZE;
		final int offset = (int)(idx % SUBSET_SIZE);

		retSubset[0] = subsetId;
		retOffset[0] = offset;
	}

	private void assign(final long idx, final long val)
	{
		final long [] subset = {0};
		final int [] offset = {0};

		convertIdxToSubsetAndOffset(idx, subset, offset);

		if (subset[0] != subsetIdx)
		{
			idxToPrimeCache.put(subsetIdx, subsetOfIdxToPrime);
			subsetOfIdxToPrime = new PrimeSubset();
			subsetOfIdxToPrime.alloc(SUBSET_SIZE);

			subsetIdx = subset[0];
		}

		subsetOfIdxToPrime.set(offset[0], val);
		maxOffset = offset[0];
	}

	/**
	 * get prime/factor from batches of indexed containers in idxToPrimeCache
	 *
	 * @param idx
	 * @return prime or -1
	 */
	@Override
	public OptionalLong retrieve(final long idx)
	{
		final long [] retSubset = {0};
		final int [] retOffset = {0};
		convertIdxToSubsetAndOffset(idx, retSubset, retOffset);

		final PrimeSubsetIntfc subset = idxToPrimeCache.get(retSubset[0]);
		return subset != null ? OptionalLong.of(subset.get(retOffset[0])) : OptionalLong.empty();
	}

	/**
	 * Load pre-generated primes.
	 *
	 *  NOTE: NOT an example of good programming style... too dense / hard to read.
	 *
	 * @return
	 */
	@Override
	public boolean primeTextloader()
	{
		final int [] index = {0};

		// Hard-code 1 into the set of values - it isn't part of the standard dataset
		// (it is not considered prime per modern definitions).
		assign(index[0]++, 1L);

		Arrays.stream(sourceFolders).forEach(
					folder ->
					{
						try ( Stream<Path> stream = Files.list(folder)) // List all folder contents
							{
					 			stream
					 				.filter(file -> !Files.isDirectory(file)) // filter out directories
									.sorted( (a, b) -> Integer.valueOf(a.getFileName().toString().split("(s|\\.)")[1]).compareTo(   // sort by #; i.e. primes2.zip , primes23.zip, etc
													   Integer.valueOf(b.getFileName().toString().split("(s|\\.)")[1]) ))
					 				.forEach( fileRef ->
					 				{
					 					statusHandler.dbgOutput("Raw text input file: %s", fileRef.toString());
	 									try
	 									{
	 										final Path tmpPath = fileRef;
	 										if (tmpPath.getFileName().toString().endsWith(".zip"))
	 										{
	 											try (ZipFile tmpZipFile = new ZipFile(tmpPath.toFile()))
	 											{
	 												tmpZipFile
	 													.entries()
	 													.asIterator()
	 													.forEachRemaining(entry ->
															{
																try( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(tmpZipFile.getInputStream(entry))))
					 											{
																	bufferedReader
																		.lines()
																		.skip(1)
																		.forEach( line ->
							 											{
																			final StringTokenizer tokenizer = new StringTokenizer(line);
																			while(tokenizer.hasMoreTokens())
																			{
																				final var token = tokenizer.nextToken();
																				assign(index[0]++, Long.parseLong(token));
																			}
							 											});
					 											}
					 											catch(final IOException e2)
					 											{
					 												statusHandler.errorOutput("%s", ZIP_FOLDER_ISSUE_MSG);
					 											}
															});
	 											}
	 											return;
	 										}
	 										else
	 										{
		 										Files
		 											.lines(tmpPath)
		 											.skip(1)
		 											.forEach( line ->
		 											{
														final StringTokenizer tokenizer = new StringTokenizer(line);
														while(tokenizer.hasMoreTokens())
														{
															assign(index[0]++, Long.parseLong(tokenizer.nextToken()));
														}
		 											});
	 										}
	 									}
	 									catch(IOException e)
	 									{
	 										statusHandler.errorOutput("%s", ZIP_FOLDER_ISSUE_MSG);
	 									}
					 				});
							}
						catch(IOException e1)
						{
							statusHandler.errorOutput("%s", ZIP_FOLDER_ISSUE_MSG);
						}
					}
				);

		idxToPrimeCache.put(subsetIdx++, subsetOfIdxToPrime);

		return true;
	}
}
