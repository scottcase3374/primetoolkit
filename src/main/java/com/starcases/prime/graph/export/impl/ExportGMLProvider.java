package com.starcases.prime.graph.export.impl;

import java.io.PrintWriter;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.graph.export.api.ExportIntfc;
import com.starcases.prime.graph.export.api.ExportsProviderIntfc;

import lombok.NonNull;

public class ExportGMLProvider implements ExportsProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("GML", "DEFAULT");

	@Override
	public ExportIntfc create(	@NonNull final PrimeSourceIntfc primeSrc,
								@NonNull final PrintWriter printWriter,
								final ImmutableMap<String,Object> settings)
	{
		return new ExportGML(primeSrc, printWriter);
	}

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
