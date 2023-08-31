package com.starcases.prime.cache.api.persistload;

import java.nio.file.Path;

import javax.cache.Cache;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.cache.api.subset.SubsetIntfc;
import com.starcases.prime.cache.api.subset.PrimeSubsetProviderIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface PersistLoaderProviderIntfc<K,J> extends SvcProviderBaseIntfc
{
	PersistLoaderIntfc create(
			@NonNull final Cache<K, SubsetIntfc<J>> cache,
			@NonNull final Path path,
			@NonNull final PrimeSubsetProviderIntfc subsetProvider,
			final ImmutableMap<String,Object> settings);
}
