package com.starcases.prime.base.nprime.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.api.PrimeBaseGeneratorIntfc;

public class NPrimeProvider implements BaseProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("NPRIME", "DEFAULT");

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	/**
	 * For NPrime, the "max-reduce" setting is required.
	 */
	@Override
	public PrimeBaseGeneratorIntfc create(final ImmutableMap<String,Object> settings)
	{
		final Integer maxReduce = (Integer)settings.getOrDefault("maxReduce", 3);

		return new BaseReduceNPrime(BaseTypes.NPRIME).assignMaxReduce(maxReduce);
	}
}
