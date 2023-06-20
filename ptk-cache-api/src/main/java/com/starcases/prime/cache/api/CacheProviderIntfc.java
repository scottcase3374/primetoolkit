package com.starcases.prime.cache.api;

import java.nio.file.Path;

import javax.cache.Cache;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;
import lombok.NonNull;

public interface CacheProviderIntfc extends SvcProviderBaseIntfc
{
	<K,V> Cache<K,V> create( @NonNull final Path cache, final boolean clearCache);
}
