package com.starcases.prime.base.primetree_impl;

import java.util.Arrays;
import java.util.Map;

import com.starcases.prime.base_api.BaseProviderIntfc;
import com.starcases.prime.base_impl.AbstractPrimeBaseGenerator;
import com.starcases.prime.core_api.PrimeSourceIntfc;
import com.starcases.prime.core_api.CollectionTrackerIntfc;

public class PrimeTreeProvider implements BaseProviderIntfc
{
	/**
	 * collTrack setting is required.
	 */
	@Override
	public AbstractPrimeBaseGenerator create(final PrimeSourceIntfc primeSrc, final Map<String, Object> settings)
	{
		final CollectionTrackerIntfc collTrack = (CollectionTrackerIntfc)settings.get("collTrack");

		return new PrimeTree(primeSrc, collTrack);
	}

	@Override
	public int countAttributesMatch(final String[] attributes)
	{
		int ret = 0;
		if (Arrays.asList(attributes).contains("PRIME_TREE"))
		{
			ret = 1;
		}
		return ret;
	}
}
