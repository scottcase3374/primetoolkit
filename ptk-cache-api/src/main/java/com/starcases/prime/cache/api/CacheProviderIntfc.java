package com.starcases.prime.cache.api;

import java.nio.file.Path;
import java.util.function.BiFunction;

import javax.cache.Cache;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface CacheProviderIntfc extends SvcProviderBaseIntfc
{
	<K,V> Cache<K,V> create( @NonNull final Path cache, @NonNull final BiFunction<K, Path, V> load, final ImmutableMap<String,Object> settings);
}
