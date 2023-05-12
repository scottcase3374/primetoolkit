package com.starcases.prime.base.triples.impl;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.PrimeBaseGenerateIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;

public class TripleProvider implements BaseProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("THREETRIPLE", "DEFAULT");

	/**
	 *
	 */
	@Override
	public PrimeBaseGenerateIntfc create(final PrimeSourceIntfc primeSrc, final ImmutableMap<String,Object> settings)
	{
		return new BaseReduceTriple(primeSrc);
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
