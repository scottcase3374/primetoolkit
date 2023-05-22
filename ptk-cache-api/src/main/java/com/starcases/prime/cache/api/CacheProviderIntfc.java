package com.starcases.prime.cache.api;

import javax.cache.Cache;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface CacheProviderIntfc extends SvcProviderBaseIntfc
{
	<X,Y> Cache<X,Y> create( @NonNull final String cacheName, final ImmutableMap<String,Object> settings);
}
