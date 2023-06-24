package com.starcases.prime.error.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.error.api.PtkErrorHandlerIntfc;
import com.starcases.prime.error.api.PtkErrorHandlerProviderIntfc;

public class PtkErrorHandlerProvider implements PtkErrorHandlerProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("ERROR_HANDLER");

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	@Override
	public PtkErrorHandlerIntfc create()
	{
		return new PtkErrorHandlerImpl();
	}
}
