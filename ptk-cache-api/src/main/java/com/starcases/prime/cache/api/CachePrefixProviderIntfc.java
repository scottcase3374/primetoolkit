package com.starcases.prime.cache.api;

import java.nio.file.Path;
import lombok.NonNull;

public interface CachePrefixProviderIntfc extends CacheProviderIntfc<Long, Long[][]>
{
	PersistedPrefixCacheIntfc create( @NonNull final Path cache, final boolean clearCache, int marker);
}
