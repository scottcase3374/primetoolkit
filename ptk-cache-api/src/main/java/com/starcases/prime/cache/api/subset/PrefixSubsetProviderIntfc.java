package com.starcases.prime.cache.api.subset;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface PrefixSubsetProviderIntfc extends SvcProviderBaseIntfc
{
	PrefixSubsetIntfc create(@NonNull long[][] prefixSubset);
}
