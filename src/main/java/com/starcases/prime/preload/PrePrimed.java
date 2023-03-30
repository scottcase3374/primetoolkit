package com.starcases.prime.preload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import com.starcases.prime.PrimeToolKit;

import org.infinispan.Cache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * manage primes which were pre-determined from another source.
 * Typically loaded from files sourced from various internet
 * sites.
 */
public final class PrePrimed
{
	/**
	 * Define the number of bits to batch together to reduce the number of
	 * cache accesses.  This is to reduce the space overhead which is
	 * significant at large scale and to a lesser extent reduce time
	 * spent in cache activities.
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
	private PrimeSubset subset = new PrimeSubset();

	/**
	 * Cache object - both in-memory and persisted data
	 */
	@Getter(AccessLevel.PRIVATE)
	private final Cache<Integer,PrimeSubset> cache;

	/**
	 * Break linear index range into indexed batches of indexed items.
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private int subsetIdx;

	private static final String ZIP_FOLDER_ISSUE_MSG = "Problem with input zip-file or folder";

	/**
	 * Constructor for class responsible for loading pre-generated prime/base info.
	 *
	 * @param sourceFolders
	 */
	public PrePrimed(final Cache<Integer, PrimeSubset> cache, final Path ... sourceFolders)
	{
		this.cache = cache;
		this.sourceFolders = sourceFolders.clone();
		subset.alloc(SUBSET_SIZE);
	}

	private void assign(final int idx, final long val)
	{
		final int offset = idx % SUBSET_SIZE;
		final int subsetId = Math.max(idx >> SUBSET_BITS, 0);
		if (subsetId != subsetIdx)
		{
			cache.put(subsetIdx++, subset);
			subset = new PrimeSubset();
			subset.alloc(SUBSET_SIZE);
		}
		subset.set(offset, val);
	}

	/**
	 * get item from batches of indexed containers in cache
	 *
	 * @param idx
	 * @return
	 */
	public long retrieve(final long idx)
	{
		final int offset = (int)(idx % SUBSET_SIZE);
		final long subsetId = Math.max(idx >> SUBSET_BITS, 0);
		return cache
				.get(subsetId)
				.get(offset);
	}

	/**
	 * Load pre-generated primes.
	 *
	 *  NOTE: NOT an example of good programming style... too dense / hard to read.
	 *
	 * @return
	 */
	public boolean load()
	{
		final int [] index = {0};

		cache
			.getAdvancedCache()
			.getStats()
			.setStatisticsEnabled(true);

		// Hard-code 1 into the set of values - it isn't part of the standard dataset (1t is not considered prime).
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
					 					PrimeToolKit.dbgOutput("file: %s", fileRef.toString());
	 									try
	 									{
	 										final Path tmpPath = fileRef;
	 										if (!tmpPath.getFileName().toString().endsWith(".zip"))
	 										{
		 										Files
		 											.lines(tmpPath)
		 											.skip(1)
		 											.forEach( line ->
		 											{
														final StringTokenizer tokenizer = new StringTokenizer(line);
														while(tokenizer.hasMoreTokens())
														{
															assign(index[0]++, Long.valueOf(tokenizer.nextToken()));
														}
		 											});
	 										}
	 										else
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
																				assign(index[0]++, Long.valueOf(tokenizer.nextToken()));
																			}
							 											});
					 											}
					 											catch(IOException e2)
					 											{
					 												PrimeToolKit.output("%s", ZIP_FOLDER_ISSUE_MSG);
					 											}
															});
	 										}}
	 									}
	 									catch(IOException e)
	 									{
	 										PrimeToolKit.output("%s", ZIP_FOLDER_ISSUE_MSG);
	 									}
					 				});
							}
						catch(IOException e1)
						{
							PrimeToolKit.output("%s", ZIP_FOLDER_ISSUE_MSG);
						}
					}
				);

		cache.put(subsetIdx++, subset);

		return true;
	}
}
