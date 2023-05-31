package com.starcases.prime.core.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.core.api.ProgressIntfc;
import com.starcases.prime.core.api.ProgressProviderIntfc;

public class ProgressTrackerProvider implements ProgressProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PROGRESS");

	@Override
	public ProgressIntfc create(final ImmutableMap<String,Object> settings)
	{
		return new ProgressTracker();
	}

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
