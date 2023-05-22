package com.starcases.prime.sql.impl;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.sql.api.CmdServerIntfc;
import com.starcases.prime.sql.api.SqlProviderIntfc;

import lombok.NonNull;

public class SqlCmdSvrProvider implements SqlProviderIntfc
{
	private static final ImmutableCollection<String> ATTRIBUTES = Lists.immutable.of("SQLPRIME");

	@Override
	public CmdServerIntfc create(@NonNull final PrimeSourceIntfc primeSrc, final int port)
	{
		return new CmdServer(primeSrc, port);
	}

	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
