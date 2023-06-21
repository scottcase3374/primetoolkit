package com.starcases.prime.cache.impl.persistload;

import java.nio.file.Path;

import javax.cache.Cache;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.cache.api.persistload.PersistLoaderIntfc;
import com.starcases.prime.cache.api.persistload.PersistLoaderProviderIntfc;
import com.starcases.prime.cache.api.subset.PrimeSubsetIntfc;
import com.starcases.prime.cache.api.subset.PrimeSubsetProviderIntfc;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class PersistLoaderProvider implements PersistLoaderProviderIntfc
{
	/**
	 * default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PERSISTLOADER");

	/**
	 * create target service.
	 */
	@Override
	public PersistLoaderIntfc create(
			@NonNull final Cache<Long, PrimeSubsetIntfc> cache,
			@NonNull final Path path,
			@NonNull final PrimeSubsetProviderIntfc subsetProvider,
			final ImmutableMap<String,Object> settings)
	{
		return new PersistedPrimeLoaderImpl(cache, path, subsetProvider);
	}

	/**
	 * get provider attributes.
	 */
	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
