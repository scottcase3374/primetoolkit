package com.starcases.prime.cache.api;

import javax.cache.Cache;

import com.starcases.prime.cache.api.subset.SubsetIntfc;
import lombok.NonNull;

public interface PersistedCacheIntfc<K,V> extends Cache<K,SubsetIntfc<V>>
{
	void persistAll();

	void persist(@NonNull final K keyVal, @NonNull final SubsetIntfc<V> subsetVal);

	long getNumCacheEntries();
}