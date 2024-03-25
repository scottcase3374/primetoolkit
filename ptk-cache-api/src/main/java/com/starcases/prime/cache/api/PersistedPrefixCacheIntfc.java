package com.starcases.prime.cache.api;

import javax.cache.Cache;

import com.starcases.prime.cache.api.subset.PrefixSubsetIntfc;
import lombok.NonNull;

public interface PersistedPrefixCacheIntfc extends Cache<Long,PrefixSubsetIntfc>
{
	void persistAll();

	void persist(@NonNull final Long keyVal, @NonNull final PrefixSubsetIntfc subsetVal);

	long getNumCacheEntries();
}