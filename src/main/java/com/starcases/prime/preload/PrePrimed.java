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

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import lombok.Getter;
import lombok.Setter;

/**
 * manage primes which were pre-determined from another source.
 * Typically loaded from files sourced from various internet
 * sites.
 */
public class PrePrimed
{
	private final static int SUBSET_BITS = 17;
	private final static int SUBSET_SIZE = 1 << SUBSET_BITS;

	/**
	 * manage any/all caching to files
	 */
	@Getter
	private static EmbeddedCacheManager cacheMgr;

	private Cache<Integer,PrimeSubset> cache = cacheMgr.getCache("primes");

	@Getter
	@Setter
	private Path [] sourceFolders;

	PrimeSubset subset = new PrimeSubset();
	private int subsetIdx = 0;

	static
	{
		try
		{
			cacheMgr = new DefaultCacheManager("infinispan.xml");
			cacheMgr.startCaches("primes");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String [] args)
	{
		Path [] paths =  {
				Path.of("/home/scott/src/eclipse/primes-ws/PrimeToolKit/data/1"),
				Path.of("/home/scott/src/eclipse/primes-ws/PrimeToolKit/data/DEFAULT")
		};

		PrePrimed pp = new PrePrimed(paths);
		pp.load();
	}

	public PrePrimed(Path [] sourceFolders)
	{
		this.sourceFolders = sourceFolders;
		subset.alloc(SUBSET_SIZE);
	}

	private void assign(int idx, long val)
	{
		int offset = idx % SUBSET_SIZE;
		int subsetId = Math.max(idx >> SUBSET_BITS, 0);
		if (subsetId != subsetIdx)
		{
			cache.put(subsetIdx++, subset);
			subset = new PrimeSubset();
			subset.alloc(SUBSET_SIZE);
		}
		subset.set(offset, val);
	}

	public long retrieve(int idx)
	{
		int offset = idx % SUBSET_SIZE;
		int subsetId = Math.max(idx >> SUBSET_BITS, 0);
		//System.out.println(String.format("parms - idx[%d] calc-ed-subset[%d] offset[%d]", idx, subsetId, offset));
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
		int [] index = {0};

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
					 					System.out.println("file: " + fileRef.toString());
	 									try
	 									{
	 										Path tmpPath = fileRef;
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
														};
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
																			};
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

		System.out.println("stats() : " + cache.getAdvancedCache().getStats().toJson());
		return true;
	}
}
