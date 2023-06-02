package com.starcases.prime.base.triples.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.api.BaseTypesProviderIntfc;

public class TripleBaseTypeProvider implements BaseTypesProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("BASE_TYPES");

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	@Override
	public ImmutableList<BaseTypesIntfc> create()
	{
		return Lists.immutable.of(TripleBaseType.values());
	}
}