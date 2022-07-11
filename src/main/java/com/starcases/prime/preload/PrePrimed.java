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
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * manage primes which were pre-determined from another source.
 * Typically loaded from files sourced from various internet
 * sites.
 */
@SuppressWarnings({"PMD.LawOfDemeter"})
public class PrePrimed
{
	/**
	 * Define the number of bits to batch together to reduce the number of
	 * cache accesses.  This is to reduce the space overhead which is
	 * significant at large scale and to a lesser extent reduce time
	 * spent in cache activities.
	 */
	@Getter(AccessLevel.PRIVATE)
	private final static int SUBSET_BITS = 17;

	/**
	 * Convert the number of bits into the size of an array for the
	 * caching.
	 */
	@Getter(AccessLevel.PRIVATE)
	private final static int SUBSET_SIZE = 1 << SUBSET_BITS;

	/**
	 * manage any/all caching to files
	 */
	@Getter
	private static EmbeddedCacheManager cacheMgr;

	/**
	 * Cache object - both in-memory and persisted data
	 */
	@Getter(AccessLevel.PRIVATE)
	private final Cache<Integer,PrimeSubset> cache = cacheMgr.getCache("primes");

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
	 * Break linear index range into indexed batches of indexed items.
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private int subsetIdx;

	static
	{
		try
		{
			cacheMgr = new DefaultCacheManager("infinispan.xml");
			cacheMgr.startCaches("primes");
		}
		catch (final IOException e)
		{
			PrimeToolKit.output("Error starting pre-prime cache: %s",e);
		}
	}

	/**
	 * main method for testing purposes
	 * @param args
	 */
	public static void main(final String [] args)
	{
		final Path [] srcPaths =  {
				Path.of("/home/scott/src/eclipse/primes-ws/PrimeToolKit/data/1"),
				Path.of("/home/scott/src/eclipse/primes-ws/PrimeToolKit/data/DEFAULT")
		};

		final PrePrimed preprimedInst = new PrePrimed(srcPaths);
		preprimedInst.load();
	}

	/**
	 * Constructor for class responsible for loading pre-generated prime/base info.
	 *
	 * @param sourceFolders
	 */
	public PrePrimed(final Path ... sourceFolders)
	{
		this.sourceFolders = sourceFolders;
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
	public long retrieve(final int idx)
	{
		final int offset = idx % SUBSET_SIZE;
		final int subsetId = Math.max(idx >> SUBSET_BITS, 0);
		return cache.get(subsetId).get(offset);
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

		cache.getAdvancedCache().getStats().setStatisticsEnabled(true);
		Arrays.stream(sourceFolders).forEach(
					folder ->
					{
						try ( Stream<Path> stream = Files.list(folder)) // List all folder contents
							{
					 			stream
					 				.filter(file -> !Files.isDirectory(file)) // filter out directories
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
																try( BufferedReader br = new BufferedReader(new InputStreamReader(tmpZipFile.getInputStream(entry))))
					 											{
																	br
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
					 											{}
															});
	 										}}
	 									}
	 									catch(IOException e)
	 									{}
					 				});
							}
						catch(IOException e1)
						{}
					}
				);

		cache.put(subsetIdx++, subset);

		PrimeToolKit.output("stats() : %s", cache.getAdvancedCache().getStats().toJson());
		return true;
	}
}
