package com.starcases.prime.base.api;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

public class BaseTypesProvider implements BaseTypesProviderIntfc
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
		return Lists.immutable.of(BaseTypes.values());
	}
}
