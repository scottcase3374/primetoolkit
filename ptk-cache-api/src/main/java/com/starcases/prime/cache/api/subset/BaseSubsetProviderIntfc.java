package com.starcases.prime.cache.api.subset;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface BaseSubsetProviderIntfc extends SvcProviderBaseIntfc
{
	SubsetIntfc<Long[][]> create(@NonNull Integer count, @NonNull long[][][] primes);
}
