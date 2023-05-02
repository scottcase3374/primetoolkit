package com.starcases.prime.graph.export.impl;

import java.io.PrintWriter;

import org.eclipse.collections.api.collection.ImmutableCollection;
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

	/**
	 * collTrack setting is required.
	 */
	@Override
	public ExportIntfc create(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final PrintWriter printWriter, final ImmutableMap<String,Object> settings)
	{
		return new ExportGML(primeSrc, printWriter);
	}

	@Override
	public int countAttributesMatch(final ImmutableCollection<String> attributes)
	{
		int ret = 0;
		if (ATTRIBUTES.containsAllIterable(attributes))
		{
			ret = ATTRIBUTES.size();
		}
		return ret;
	}
}