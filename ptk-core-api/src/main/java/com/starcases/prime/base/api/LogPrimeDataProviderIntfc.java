package com.starcases.prime.base.api;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.core.api.LogPrimeDataIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public interface LogPrimeDataProviderIntfc extends SvcProviderBaseIntfc
{
	LogPrimeDataIntfc create(@NonNull final PrimeSourceIntfc primeSrc, final ImmutableMap<String,Object> settings);
}
