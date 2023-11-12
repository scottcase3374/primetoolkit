package com.starcases.prime.metrics.impl;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.metrics.api.MetricsRegistryProviderIntfc;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteMeterRegistry;

public class GraphiteRegistryProvider implements MetricsRegistryProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("GRAPHITE", "METRICS");



	@Override
	public MetricsRegistryProviderIntfc create(final ImmutableMap<String, Object> attributes)
	{
		final GraphiteConfig graphiteConfig = new GraphiteConfig()
		{
		    @Override
		    public String host()
		    {
		        return "localhost";
		    }

		    @Override
		    public String get(final String k)
		    {
		        return null; // accept the rest of the defaults
		    }

		    @Override
		    public String [] tagsAsPrefix()
		    {
		    	return new String[] {"TASK" };
		    }
		};
		MetricProvider.register(new GraphiteMeterRegistry(graphiteConfig, Clock.SYSTEM, HierarchicalNameMapper.DEFAULT));
		return this;
	}

	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
