package com.starcases.prime.preload.impl;

import java.nio.file.Path;
import java.util.Optional;

import javax.cache.Cache;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.preload.api.PreloaderIntfc;
import com.starcases.prime.preload.api.PreloaderProviderIntfc;
import com.starcases.prime.preload.api.PrimeSubsetIntfc;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class PreloaderProvider implements PreloaderProviderIntfc
{
	/**
	 * default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRELOADER");

	/**
	 * create target service.
	 */
	@Override
	public Optional<PreloaderIntfc> create(@NonNull final Cache<Long, PrimeSubsetIntfc> cache, @NonNull final Path path, final ImmutableMap<String,Object> settings)
	{
		final var  preloader = new PrimePreloaderImpl(cache, path);

		Optional<PreloaderIntfc> ret = Optional.empty();
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
