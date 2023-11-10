package com.starcases.prime.kern.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;

public class StatusHandlerProvider implements StatusHandlerProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("STATUS_HANDLER");

	public StatusHandlerProvider()
	{ /* nothing to do */ }

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	@Override
	public StatusHandlerIntfc create()
	{
		return new StatusHandlerImpl();
	}
}
