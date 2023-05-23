package com.starcases.prime.metrics.impl;

import java.util.logging.Logger;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.metrics.api.MetricIntfc;
import com.starcases.prime.metrics.api.MetricsProviderIntfc;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class SimpleMeterRegistryProvider implements MetricsProviderIntfc, MetricIntfc
{
	private static final Logger LOG = Logger.getLogger(SimpleMeterRegistryProvider.class.getName());

	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("SIMPLE_METER", "REGISTRY");

	@Override
	public MetricIntfc create(final ImmutableMap<String, Object> attributes)
	{
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
			LOG.fine("Enabling simple registry");
			MetricMonitor.register(new SimpleMeterRegistry());
		}
	}

	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
