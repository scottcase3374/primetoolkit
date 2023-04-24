package com.starcases.prime.base.nprime_impl;

import java.util.Arrays;
import java.util.Map;

import com.starcases.prime.base_api.BaseProviderIntfc;
import com.starcases.prime.base_impl.AbstractPrimeBaseGenerator;
import com.starcases.prime.core_api.PrimeSourceIntfc;

public class NPrimeProvider implements BaseProviderIntfc
{
	/**
	 * For NPrime, the "maxreduce" setting is required.
	 */
	@Override
	public AbstractPrimeBaseGenerator create(final PrimeSourceIntfc primeSrc, final Map<String, Object> settings)
	{
		final Integer maxReduce = (Integer)settings.getOrDefault("maxReduce", 3);

		return new BaseReduceNPrime(primeSrc).assignMaxReduce(maxReduce);
	}

	@Override
	public int countAttributesMatch(final String[] attributes)
	{
		int ret = 0;
		if (Arrays.asList(attributes).contains("NPRIME"))
		{
			ret = 1;
		}
		return ret;
	}
}
