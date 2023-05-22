package com.starcases.prime.base.nprime.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.api.LogPrimeDataProviderIntfc;
import com.starcases.prime.core.api.LogPrimeDataIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;

import lombok.NonNull;

public class LogNPrimeDataProvider implements LogPrimeDataProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("NPRIME", "DEFAULT");

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}


	@Override
	public LogPrimeDataIntfc create(@NonNull final PrimeSourceIntfc primeSrc, final ImmutableMap<String,Object> settings)
	{
		return new LogBasesNPrime(primeSrc);
	}
}
