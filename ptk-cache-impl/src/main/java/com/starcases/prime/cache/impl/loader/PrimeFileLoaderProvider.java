package com.starcases.prime.cache.impl.loader;

import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.cache.api.preload.PrimeFileloaderIntfc;
import com.starcases.prime.cache.api.PrimeSubsetCacheIntfc;
import com.starcases.prime.cache.api.preload.PrimeFileLoaderProviderIntfc;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class PrimeFileLoaderProvider implements PrimeFileLoaderProviderIntfc
{
	/**
	 * default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRELOADER");

	/**
	 * create target service.
	 */
	@Override
	public Optional<PrimeFileloaderIntfc> create(@NonNull final PrimeSubsetCacheIntfc<Long> cache, @NonNull final Path path, final ImmutableMap<String,Object> settings)
	{
		final var  preloader = new PrimeFileLoaderImpl(cache, path);

		Optional<PrimeFileloaderIntfc> ret = Optional.empty();
		if (preloader.primeTextloader())
		{
			ret = Optional.of(preloader);
		}
		return ret;
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
