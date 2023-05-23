package com.starcases.prime.metrics.impl;

import java.util.logging.Logger;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.metrics.api.MetricIntfc;
import com.starcases.prime.metrics.api.MetricsProviderIntfc;

import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;

public class JvmGCMetricProvider implements MetricsProviderIntfc, MetricIntfc
{
	private static final Logger LOG = Logger.getLogger(JvmGCMetricProvider.class.getName());

	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("JVMGC", "METRICS");

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
			LOG.fine("Enabling JVM GC");
			MetricMonitor.bind(r -> new JvmGcMetrics().bindTo(r));
		}
	}

	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
