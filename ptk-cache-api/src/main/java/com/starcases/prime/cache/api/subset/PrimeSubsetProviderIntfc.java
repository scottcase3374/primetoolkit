package com.starcases.prime.cache.api.subset;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface PrimeSubsetProviderIntfc extends SvcProviderBaseIntfc
{
	SubsetIntfc<Long> create(@NonNull long[] primes);
}
