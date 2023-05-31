package com.starcases.prime.metrics.impl;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.metrics.api.MetricsRegistryProviderIntfc;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class SimpleMeterRegistryProvider implements MetricsRegistryProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("SIMPLE_METER", "REGISTRY");

	@Override
	public MetricsRegistryProviderIntfc create(final ImmutableMap<String, Object> attributes)
	{
		MetricProvider.register(new SimpleMeterRegistry());
		return this;
	}

	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
