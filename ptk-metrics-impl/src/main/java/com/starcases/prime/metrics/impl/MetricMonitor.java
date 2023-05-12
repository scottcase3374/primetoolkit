package com.starcases.prime.metrics.impl;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.metrics.api.MetricsProviderIntfc;
import com.starcases.prime.service.SvcLoader;
import com.starcases.prime.shared.api.OutputableIntfc;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Class for handling metric data gathered by various processes.
 *
 * TODO Still deciding on requirements for this. This generates
 * some metrics to the log but there is plenty of work to do.
 *
 */
public final class MetricMonitor
{
	private static final Logger LOG = Logger.getLogger(MetricMonitor.class.getName());

	/**
	 *  Metrics data registry (drop wizard)
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final CompositeMeterRegistry METRICS_REGISTRY;

	static
	{
		METRICS_REGISTRY = new CompositeMeterRegistry();
		final SvcLoader<MetricsProviderIntfc, Class<MetricsProviderIntfc>> registryProviders = new SvcLoader< >(MetricsProviderIntfc.class);
		final ImmutableCollection<String> attributes = Lists.immutable.of("REGISTRY");
		registryProviders.providers(attributes).forEach(p -> p.create(null).enable(true));
	}

	public static void register(final MeterRegistry registry)
	{
		if (LOG.isLoggable(Level.FINE))
		{
			LOG.fine("Added a registry");
		}
		METRICS_REGISTRY.add(registry);
	}

	public static void bind(final Consumer<MeterRegistry> bindMetrics)
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
	private MetricMonitor()
	{
		// Nothing to do; uses static interfaces
	}

	/**
	 * Perform a timing using a specific timer.  Use try-with-resources.
	 *
	 * @param outputable
	 * @return
	 */
	public static Optional<Timer> timer(final OutputableIntfc outputable, final String...tags)
	{
		return Optional.ofNullable(METRICS_REGISTRY.timer(outputable.toString(), tags));
	}

	/**
	 * Perform a timing using a specific long timer.
	 * @param outputable
	 * @return
	 */
	public static Optional<LongTaskTimer.Sample> longTimer(final OutputableIntfc outputable, final String...tags)
	{
		return Optional.ofNullable(LongTaskTimer.builder(outputable.toString()).register(METRICS_REGISTRY).start());
	}
}
