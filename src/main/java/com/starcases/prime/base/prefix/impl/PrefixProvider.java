package com.starcases.prime.base.prefix.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.impl.PrimeBaseGenerator;

public class PrefixProvider implements BaseProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PREFIX", "DEFAULT");

	/**
	 *
	 */
	@Override
	public PrimeBaseGenerator create(final ImmutableMap<String,Object> settings)
	{
		return new BasePrefixes(BaseTypes.PREFIX);
	}

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
