package com.starcases.prime.cache.api;

import java.nio.file.Path;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;
import lombok.NonNull;

public interface CacheProviderIntfc extends SvcProviderBaseIntfc
{
	<K> PrimeSubsetCacheIntfc<K> create( @NonNull final Path cache, final boolean clearCache);
}
