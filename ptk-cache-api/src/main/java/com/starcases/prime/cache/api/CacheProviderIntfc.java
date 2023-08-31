package com.starcases.prime.cache.api;

import java.nio.file.Path;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;
import lombok.NonNull;

public interface CacheProviderIntfc<T> extends SvcProviderBaseIntfc
{
	PersistedCacheIntfc<T> create( @NonNull final Path cache, final boolean clearCache);
}
