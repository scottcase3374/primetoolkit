package com.starcases.prime.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.starcases.prime.intfc.OutputableIntfc;

import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Class for handling metric data gathered by various processes
 */
public class MetricMonitor
{

	/**
	 *  Metrics data registry (drop wizard)
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final MetricRegistry metrics = new MetricRegistry();

	/**
	 * map from some framework identity type to a timer.
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final Map<OutputableIntfc, Timer> timers = new HashMap<>();

	/**
	 * Empty constructor
	 */
	public MetricMonitor()
	{
		// nothing else to do
	}

	/**
	 * Create a mapping for a timer.
	 *
	 * @param outputable
	 * @param desc
	 */
	public static void addTimer(final OutputableIntfc outputable, final String desc)
	{
		timers.put(outputable, metrics.timer(desc));
	}

	/**
	 * Perform a timing using a specify timer.  Use try-with-resources.
	 *
	 * @param outputable
	 * @return
	 */
	public static Optional<Timer.Context> time(final OutputableIntfc outputable)
	{
		return Optional.ofNullable(timers.get(outputable).time());
	}

	/**
	 * Start output
	 */
	public static void startReport()
	{
		final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
                .outputTo(LoggerFactory.getLogger("com.starcases.prime.metrics"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
		reporter.start(15, TimeUnit.SECONDS);
	  }

}
