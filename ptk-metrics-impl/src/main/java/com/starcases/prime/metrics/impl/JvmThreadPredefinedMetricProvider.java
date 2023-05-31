package com.starcases.prime.metrics.impl;

import java.util.logging.Logger;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.metrics.api.PredefinedMetricsProviderIntfc;

import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;

public class JvmThreadPredefinedMetricProvider implements PredefinedMetricsProviderIntfc
{
	private static final Logger LOG = Logger.getLogger(JvmThreadPredefinedMetricProvider.class.getName());

	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("JVMTHREAD", "METRICS");

	@Override
	public String getProviderName()
	{
		return this.getClass().getName();
	}

	@Override
	public void create(ImmutableMap<String, Object> attributes)
	{
		LOG.fine("Enabling JVM Thread");
		MetricProvider.bind(r -> new JvmThreadMetrics().bindTo(r));
	}

	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
