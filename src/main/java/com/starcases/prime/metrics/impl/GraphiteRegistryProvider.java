package com.starcases.prime.metrics.impl;

import java.util.logging.Logger;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.metrics.MetricMonitor;
import com.starcases.prime.metrics.api.MetricIntfc;
import com.starcases.prime.metrics.api.MetricsProviderIntfc;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteMeterRegistry;

public class GraphiteRegistryProvider implements MetricsProviderIntfc, MetricIntfc
{
	private static final Logger LOG = Logger.getLogger(GraphiteRegistryProvider.class.getName());

	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("GRAPHITE", "REGISTRY");

	private GraphiteConfig graphiteConfig;

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

	@Override
	public MetricIntfc create(final ImmutableMap<String, Object> attributes)
	{
		graphiteConfig = new GraphiteConfig()
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

		return this;
	}

	@Override
	public String getProviderName()
	{
		return this.getClass().getName();
	}

	@Override
	public void enable(final boolean enabled)
	{
		if (enabled)
		{
			LOG.fine("Enabling Graphite");
			MetricMonitor.register(new GraphiteMeterRegistry(graphiteConfig, Clock.SYSTEM, HierarchicalNameMapper.DEFAULT));
		}
	}
}
