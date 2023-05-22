package com.starcases.prime.base.triples.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.api.PrimeBaseGeneratorIntfc;

public class TripleProvider implements BaseProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("THREETRIPLE", "DEFAULT");

	/**
	 *
	 */
	@Override
	public PrimeBaseGeneratorIntfc create(final ImmutableMap<String,Object> settings)
	{
		return new BaseReduceTriple(BaseTypes.THREETRIPLE);
	}

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
