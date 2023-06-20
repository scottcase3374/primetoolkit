package com.starcases.prime.preload.api;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface PrimeSubsetProviderIntfc extends SvcProviderBaseIntfc
{
	PrimeSubsetIntfc create(@NonNull Integer count, @NonNull long[] primes);
}
