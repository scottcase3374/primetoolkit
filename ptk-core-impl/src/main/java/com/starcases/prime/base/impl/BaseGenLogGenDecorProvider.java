package com.starcases.prime.base.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.base.api.BaseGenDecorProviderIntfc;

import com.starcases.prime.base.api.BaseGenIntfc;

import lombok.NonNull;

public class BaseGenLogGenDecorProvider implements BaseGenDecorProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("LOG_BASE_GENERATOR_DECORATOR");

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	@Override
	public BaseGenIntfc create(@NonNull final BaseGenIntfc baseGen)
	{
		return new LogBaseGenDecor(baseGen);
	}
}
