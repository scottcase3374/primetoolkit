package com.starcases.prime.sql.csvoutput.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.sql.api.OutputProviderIntfc;
import com.starcases.prime.sql.api.OutputServiceIntfc;
import com.starcases.prime.sql.api.PrimeResultIntfc;

public class CSVOutputProvider implements OutputProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("text/csv".toUpperCase());

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	@Override
	public OutputServiceIntfc create(final PrimeSourceIntfc primeSrc, final PrimeResultIntfc result)
	{
		return new CSVOutputSvcImpl().init(primeSrc, result);
	}
}