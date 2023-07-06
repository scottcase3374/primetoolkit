package com.starcases.prime.base.nprime.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;

public class NPrimeBaseTypeProvider implements BaseTypesProviderIntfc
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
		return Lists.immutable.of(NPrimeBaseType.values());
	}
}