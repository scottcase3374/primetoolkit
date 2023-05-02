package com.starcases.prime.preload.api;

import java.nio.file.Path;

import org.eclipse.collections.api.map.ImmutableMap;
import org.infinispan.Cache;

import com.starcases.prime.preload.impl.PrimeSubset;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface PreloaderProviderIntfc extends SvcProviderBaseIntfc
{
	<K,V> PreloaderIntfc create(@NonNull final Cache<Long, PrimeSubset> cache, @NonNull final Path path, final ImmutableMap<String,Object> settings);
}
