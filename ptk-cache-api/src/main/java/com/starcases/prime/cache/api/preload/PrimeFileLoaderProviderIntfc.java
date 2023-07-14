package com.starcases.prime.cache.api.preload;

import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.cache.api.PrimeSubsetCacheIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface PrimeFileLoaderProviderIntfc extends SvcProviderBaseIntfc
{
	Optional<PrimeFileloaderIntfc> create(@NonNull final PrimeSubsetCacheIntfc<Long> cache, @NonNull final Path path, final ImmutableMap<String,Object> settings);
}
