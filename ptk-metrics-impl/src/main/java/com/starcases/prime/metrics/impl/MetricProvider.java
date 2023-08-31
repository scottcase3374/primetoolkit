package com.starcases.prime.metrics.impl;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.kern.api.OutputableIntfc;
import com.starcases.prime.metrics.api.MetricIntfc;
import com.starcases.prime.metrics.api.MetricProviderIntfc;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * Class for handling metric data gathered by various processes.
 *
 * TODO Still deciding on requirements for this. This generates
 * some metrics to the log but there is plenty of work to do.
 *
 */
public final class MetricProvider implements MetricProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of( "METRIC_PROVIDER");

	private static final Logger LOG = Logger.getLogger(MetricProvider.class.getName());

	/**
	 *  Metrics data registry (drop wizard)
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final CompositeMeterRegistry METRICS_REGISTRY;

	// TODO make metric registries configurable
	static
	{
		METRICS_REGISTRY = new CompositeMeterRegistry();
		register((new SimpleMeterRegistry()));
		register((new LoggingMeterRegistry()));
	}

	@Override
	public MetricProviderIntfc create(ImmutableMap<String, Object> settings)
	{
		return new MetricProvider();
	}

	public static void register(@NonNull final MeterRegistry registry)
	{
		if (LOG.isLoggable(Level.FINE))
		{
			LOG.fine("Added a registry");
		}
		METRICS_REGISTRY.add(registry);
	}

	public static void bind(@NonNull final Consumer<MeterRegistry> bindMetrics)
	{
		if (LOG.isLoggable(Level.FINE))
		{
			LOG.fine("A metric Source was bound.");
		}
		bindMetrics.accept(METRICS_REGISTRY);
	}

	/**
	 * Empty constructor
	 */
	public MetricProvider()
	{
		// Nothing to do
	}

	/**
	 * Perform a timing using a specific timer.  Use try-with-resources.
	 *
	 * @param outputable
	 * @return
	 */
	public MetricIntfc timer(@NonNull final OutputableIntfc outputable, @NonNull final String...tags)
	{
		return new MetricTimerWrapper(METRICS_REGISTRY.timer(outputable.toString(), tags));
	}

	/**
	 * Perform a timing using a specific long timer.
	 * @param outputable
	 * @return
	 */
	public MetricIntfc  longTimer(@NonNull final OutputableIntfc outputable, @NonNull final String...tags)
	{

		return new MetricLongTimerWrapper(LongTaskTimer.builder(String.format("%s [%s]", outputable, Arrays.toString(tags))).register(METRICS_REGISTRY).start());
	}

	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
