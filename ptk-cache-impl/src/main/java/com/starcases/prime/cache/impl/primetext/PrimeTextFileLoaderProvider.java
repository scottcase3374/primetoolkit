package com.starcases.prime.cache.impl.primetext;

import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.cache.api.PersistedCacheIntfc;
import com.starcases.prime.cache.api.primetext.PrimeTextFileLoaderProviderIntfc;
import com.starcases.prime.cache.api.primetext.PrimeTextFileloaderIntfc;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class PrimeTextFileLoaderProvider implements PrimeTextFileLoaderProviderIntfc
{
	/**
	 * default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRELOADER");

	/**
	 * create target service.
	 */
	@Override
	public Optional<PrimeTextFileloaderIntfc> create(@NonNull final PersistedCacheIntfc<Long,Long> cache, @NonNull final Path path, final ImmutableMap<String,Object> settings)
	{
		final var  preloader = new PrimeTextFileLoaderImpl(cache, path);

		Optional<PrimeTextFileloaderIntfc> ret = Optional.empty();
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
