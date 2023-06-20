package com.starcases.prime.preload.api;

import java.nio.file.Path;
import java.util.Optional;

import javax.cache.Cache;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface PreloaderProviderIntfc extends SvcProviderBaseIntfc
{
	Optional<PreloaderIntfc> create(@NonNull final Cache<Long, PrimeSubsetIntfc> cache, @NonNull final Path path, final ImmutableMap<String,Object> settings);
}
