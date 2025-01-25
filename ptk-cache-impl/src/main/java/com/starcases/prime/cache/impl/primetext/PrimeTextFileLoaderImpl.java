package com.starcases.prime.cache.impl.primetext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.OptionalLong;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import org.eclipse.collections.api.factory.Lists;
import org.mapdb.BTreeMap;

import com.starcases.prime.cache.api.primetext.PrimeTextFileloaderIntfc;

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
class PrimeTextFileLoaderImpl implements PrimeTextFileloaderIntfc
{
	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();


	/**
	 * Paths to source folders containing files of any pre-computed prime/base info.
	 */
	@Getter
	@Setter
	private Path [] sourceFolders;

	/**
	 * Cache object - both in-memory and persisted data
	 * 	Map index to prime#.
	 */
	@Getter(AccessLevel.PRIVATE)
	private final BTreeMap<Long,Long> idxToPrimeCache;



	private static final String ZIP_FOLDER_ISSUE_MSG = "Problem with input zip-file or folder";

	/**
	 * Constructor for class responsible for loading pre-generated prime/base info.
	 *
	 * @param sourceFolders
	 */
	public PrimeTextFileLoaderImpl(@NonNull final BTreeMap<Long,Long> idxToPrimeCache, final Path ... sourceFolders)
	{
		this.idxToPrimeCache = idxToPrimeCache;
		this.sourceFolders = sourceFolders;
	}

	private void assign(final long idx, final long val)
	{
		idxToPrimeCache.putIfAbsent(idx, val);
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
		var ret = idxToPrimeCache.get(idx);
		return ret != null ? OptionalLong.of(ret) : OptionalLong.empty();
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
		if (null == sourceFolders)
		{
			statusHandler.errorOutput("Source folders is not defined.");
			return false;
		}
		else
		{
			statusHandler.dbgOutput("Raw text source folder: %s", Arrays.stream(sourceFolders).map(Object::toString).collect(Collectors.joining()));
		}

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
									.sorted( (a, b) -> Integer.valueOf(a.getFileName().toString().split("[s.]")[1]).compareTo(   // sort by #; i.e. primes2.zip , primes23.zip, etc
													   Integer.valueOf(b.getFileName().toString().split("[s.]")[1]) ))
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

		return true;
	}
}
