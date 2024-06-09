package com.starcases.prime.cache.api;

import java.nio.file.Path;

import javax.cache.CacheManager;
import javax.cache.spi.CachingProvider;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;
import lombok.NonNull;

public interface CacheProviderIntfc<K,V> extends SvcProviderBaseIntfc, CacheManager, CachingProvider
{
	PersistedCacheIntfc<K,V> create( @NonNull final Path cache, final boolean clearCache);
}
