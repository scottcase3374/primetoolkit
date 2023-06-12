package com.starcases.prime.preload.impl;

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

import com.starcases.prime.common.api.PTKLogger;
import com.starcases.prime.preload.api.PreloaderIntfc;
import com.starcases.prime.preload.api.PrimeSubset;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


/**
 * manage primes which were pre-determined from another source.
 * Typically loaded from files sourced from various internet
 * sites.
 */
class PrimeLoader implements PreloaderIntfc
{
	Logger LOG = Logger.getLogger(PrimeLoader.class.getName());

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
	 * represents max-assigned idx
	 */
	@Getter
	private long maxIdx = -1;

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
	private final Cache<Long,PrimeSubset> idxToPrimeCache;

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
	public PrimeLoader(final Cache<Long, PrimeSubset> idxToPrimeCache, final Path ... sourceFolders)
	{
		this.idxToPrimeCache = idxToPrimeCache;
		this.sourceFolders = sourceFolders.clone();
		subsetOfIdxToPrime.alloc(SUBSET_SIZE);
	}

	/**
	 * Centralized the mapping from requested idx to subset/offset values.
	 *
	 * @param idx
	 * @param retSubset
	 * @param retOffset
	 */
	private void convertIdxToSubsetAndOffset(final long idx, final long [] retSubset, final int [] retOffset)
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
			//new Primes()
			//new PrimeSubsetAsn().setPrimes();
			subsetOfIdxToPrime = new PrimeSubset();
			subsetOfIdxToPrime.alloc(SUBSET_SIZE);

			subsetIdx = subset[0];
		}
		idxToPrimeCache.put(subsetIdx, subsetOfIdxToPrime);
		subsetOfIdxToPrime.set(offset[0], val);
		maxIdx = idx;
	}


	private void done()
	{
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

		final var subset = idxToPrimeCache.get(retSubset[0]);
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
	public boolean load()
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
					 					PTKLogger.dbgOutput("file: %s", fileRef.toString());
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
					 												PTKLogger.output("%s", ZIP_FOLDER_ISSUE_MSG);
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
	 										PTKLogger.output("%s", ZIP_FOLDER_ISSUE_MSG);
	 									}
					 				});
							}
						catch(IOException e1)
						{
							PTKLogger.output("%s", ZIP_FOLDER_ISSUE_MSG);
						}
					}
				);

		idxToPrimeCache.put(subsetIdx++, subsetOfIdxToPrime);

		done();

		return true;
	}
}
