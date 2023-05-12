package com.starcases.prime.base.api;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface BaseProviderIntfc extends SvcProviderBaseIntfc
{
	PrimeBaseGenerateIntfc create( @NonNull final PrimeSourceIntfc primeSrc, final ImmutableMap<String,Object> settings);
}
