package com.starcases.prime.base.nprime.impl;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.impl.AbstractPrimeBaseGenerator;
import com.starcases.prime.core.api.PrimeSourceIntfc;

public class NPrimeProvider implements BaseProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("NPRIME", "DEFAULT");

	/**
	 * For NPrime, the "maxreduce" setting is required.
	 */
	@Override
	public AbstractPrimeBaseGenerator create(final PrimeSourceIntfc primeSrc, final ImmutableMap<String,Object> settings)
	{
		final Integer maxReduce = (Integer)settings.getOrDefault("maxReduce", 3);

		return new BaseReduceNPrime(primeSrc).assignMaxReduce(maxReduce);
	}

	@Override
	public int countAttributesMatch(final ImmutableCollection<String> attributes)
	{
		int ret = 0;
		if (ATTRIBUTES.containsAllIterable(attributes))
		{
			ret = ATTRIBUTES.size();
		}
		return ret;
	}
}
