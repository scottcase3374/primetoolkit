package com.starcases.prime.metrics;

import java.util.Optional;
import java.util.logging.Logger;

import com.starcases.prime.intfc.OutputableIntfc;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteMeterRegistry;
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


	public static void enableGraphiteRegistry(boolean enable)
	{
		if (enable)
		{
			GraphiteConfig graphiteConfig = new GraphiteConfig()
			{
			    @Override
			    public String host()
			    {
			        return "localhost";
			    }

			    @Override
			    public String get(String k)
			    {
			        return null; // accept the rest of the defaults
			    }

			    @Override
			    public String [] tagsAsPrefix()
			    {
			    	return new String[] {"TASK" };
			    }
			};

			metricsRegistry.add(new GraphiteMeterRegistry(graphiteConfig, Clock.SYSTEM, HierarchicalNameMapper.DEFAULT));
		}
	}

	public static void enableSimpleRegistry(boolean enable)
	{
		if (enable)
		{
			metricsRegistry.add(new SimpleMeterRegistry());
		}
	}

	public static void enableLoggerRegistry(boolean enable)
	{
		if (enable)
		{
			final var loggingRegistry = new LoggingMeterRegistry();
			metricsRegistry.add(loggingRegistry);
		}
	}

	public static void enableJvmMemoryMetric(boolean enable)
	{
		if (enable)
		{
			new JvmMemoryMetrics().bindTo(metricsRegistry);
		}
	}

	public static void enableJvmGcMetric(boolean enable)
	{
		if (enable)
		{
			new JvmGcMetrics().bindTo(metricsRegistry);
		}
	}

	public static void enableJvmThreadMetric(boolean enable)
	{
		if (enable)
		{
			new JvmThreadMetrics().bindTo(metricsRegistry);
		}
	}

	public static void enableProcessorMetric(boolean enable)
	{
		if (enable)
		{
			new ProcessorMetrics().bindTo(metricsRegistry);
		}
	}

	public static void enableAll(boolean enable)
	{
		enableSimpleRegistry(enable);
		enableLoggerRegistry(enable);
		enableGraphiteRegistry(enable);

		enableJvmMemoryMetric(enable);
		enableJvmGcMetric(enable);
		enableJvmThreadMetric(enable);
		enableProcessorMetric(enable);
	}

	/**
	 *  Metrics data registry (drop wizard)
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final CompositeMeterRegistry metricsRegistry;

	static
	{
		metricsRegistry = new CompositeMeterRegistry();
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
		return Optional.ofNullable(metricsRegistry.timer(outputable.toString(), tags));
	}

	/**
	 * Perform a timing using a specific long timer.
	 * @param outputable
	 * @return
	 */
	public static Optional<LongTaskTimer.Sample> longTimer(final OutputableIntfc outputable, final String...tags)
	{
		return Optional.ofNullable(LongTaskTimer.builder(outputable.toString()).register(metricsRegistry).start());
	}
}
