package com.starcases.prime.cache.api.persistload;

import java.nio.file.Path;

import javax.cache.Cache;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.cache.api.subset.PrefixSubsetIntfc;
import com.starcases.prime.cache.api.subset.PrefixSubsetProviderIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface PersistPrefixLoaderProviderIntfc extends SvcProviderBaseIntfc
{
	PersistLoaderIntfc create(
			@NonNull final Cache<Long, PrefixSubsetIntfc> cache,
			@NonNull final Path path,
			@NonNull final PrefixSubsetProviderIntfc subsetProvider,
			final ImmutableMap<String,Object> settings);
}
