package com.starcases.prime.preload.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.OptionalLong;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import javax.cache.Cache;

import com.starcases.prime.core.impl.PTKLogger;
import com.starcases.prime.preload.api.PreloaderIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * manage primes which were pre-determined from another source.
 * Typically loaded from files sourced from various internet
 * sites.
 */
public final class PrimeLoader implements PreloaderIntfc
{
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

	private void assign(final long idx, final long val)
	{
		final int offset = (int)(idx % SUBSET_SIZE);
		final long subsetId = Math.max(idx >> SUBSET_BITS, 0);
		if (subsetId != subsetIdx)
		{
			idxToPrimeCache.put(subsetIdx,   subsetOfIdxToPrime);

			subsetOfIdxToPrime = new PrimeSubset();
			subsetOfIdxToPrime.alloc(SUBSET_SIZE);
		}
		subsetOfIdxToPrime.set(offset, val);
		maxIdx = idx;
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
		final int offset = (int)(idx % SUBSET_SIZE);
		final long subsetId = Math.max(idx >> SUBSET_BITS, 0);
		final var subset = idxToPrimeCache.get(subsetId);
		return subset != null ? OptionalLong.of(subset.get(offset)) : OptionalLong.empty();
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

		// Hard-code 1 into the set of values - it isn't part of the standard dataset (it is not considered prime per modern definitions).
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
																				assign(index[0]++, Long.parseLong(tokenizer.nextToken()));
																			}
							 											});
					 											}
					 											catch(final IOException e2)
					 											{
					 												PTKLogger.output("%s", ZIP_FOLDER_ISSUE_MSG);
					 											}
															});
	 											}
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

		return true;
	}
}
