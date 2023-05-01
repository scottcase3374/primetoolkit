package com.starcases.prime.metrics.impl;

import java.util.logging.Logger;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.metrics.MetricMonitor;
import com.starcases.prime.metrics.api.MetricIntfc;
import com.starcases.prime.metrics.api.MetricsProviderIntfc;

import io.micrometer.core.instrument.binder.system.ProcessorMetrics;

public class ProcessorMetricProvider implements MetricsProviderIntfc, MetricIntfc
{
	private static final Logger LOG = Logger.getLogger(ProcessorMetricProvider.class.getName());

	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PROCESSOR", "METRICS");

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
			LOG.fine("Enabling Processor ");
			MetricMonitor.bind(r -> new ProcessorMetrics().bindTo(r));
		}
	}
}
