package com.starcases.prime.cache.api;

import javax.cache.Cache;

import com.starcases.prime.cache.api.subset.SubsetIntfc;
import lombok.NonNull;

public interface PersistedCacheIntfc<T> extends Cache<Long,SubsetIntfc<T>>
{
	void persistAll();

	void persist(@NonNull final Long keyVal, @NonNull final SubsetIntfc<T> subsetVal);

	long getNumCacheEntries();
}