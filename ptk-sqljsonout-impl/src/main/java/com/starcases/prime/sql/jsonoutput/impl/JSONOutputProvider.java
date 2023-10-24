package com.starcases.prime.sql.jsonoutput.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.sql.api.OutputProviderIntfc;
import com.starcases.prime.sql.api.OutputServiceIntfc;
import com.starcases.prime.sql.api.PrimeSqlResultIntfc;

public class JSONOutputProvider implements OutputProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("application/json".toUpperCase());

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	@Override
	public OutputServiceIntfc create(final PrimeSourceIntfc primeSrc, final PrimeSqlResultIntfc result)
	{
		return new JSONOutputSvcImpl().init(primeSrc, result);
	}
}