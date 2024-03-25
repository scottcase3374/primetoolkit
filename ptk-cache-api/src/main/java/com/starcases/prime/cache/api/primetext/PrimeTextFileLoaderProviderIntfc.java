package com.starcases.prime.cache.api.primetext;

import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.cache.api.PersistedCacheIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface PrimeTextFileLoaderProviderIntfc extends SvcProviderBaseIntfc
{
	Optional<PrimeTextFileloaderIntfc> create(@NonNull final PersistedCacheIntfc<Long,Long> cache, @NonNull final Path path, final ImmutableMap<String,Object> settings);
}
