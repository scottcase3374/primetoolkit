package com.starcases.prime.base.triples_impl;

import java.util.Arrays;
import java.util.Map;

import com.starcases.prime.base_api.BaseProviderIntfc;
import com.starcases.prime.base_impl.AbstractPrimeBaseGenerator;
import com.starcases.prime.core_api.PrimeSourceIntfc;

public class TripleProvider implements BaseProviderIntfc
{
	/**
	 *
	 */
	@Override
	public AbstractPrimeBaseGenerator create(final PrimeSourceIntfc primeSrc, final Map<String, Object> settings)
	{
		return new BaseReduceTriple(primeSrc);
	}

	@Override
	public int countAttributesMatch(final String[] attributes)
	{
		int ret = 0;
		if (Arrays.asList(attributes).contains("THREETRIPLE"))
		{
			ret = 1;
		}
		return ret;
	}
}
