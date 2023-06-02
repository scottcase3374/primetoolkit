package com.starcases.prime.base.nprime.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseGenFactoryIntfc;

/**
 *
 * @author scott
 *
 */
public class NPrimeProvider implements BaseProviderIntfc
{
	/**
	 * attributes for default provider.
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("NPRIME", "DEFAULT");

	/**
	 * constructor - Nothing to do.
	 */
	public NPrimeProvider()
	{}

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	/**
	 * For NPrime, the "max-reduce" setting is required.
	 */
	@Override
	public BaseGenFactoryIntfc create(final ImmutableMap<String,Object> settings)
	{
		final Integer maxReduce = (Integer)settings.getOrDefault("maxReduce", 3);

		return new BaseReduceNPrime(NPrimeBaseType.NPRIME).assignMaxReduce(maxReduce);
	}
}
