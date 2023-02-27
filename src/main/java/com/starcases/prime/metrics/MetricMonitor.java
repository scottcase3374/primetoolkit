package com.starcases.prime.metrics;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.starcases.prime.intfc.OutputableIntfc;

import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Class for handling metric data gathered by various processes.
 *
 * TODO Some revamp needed. Use logger instead of console. Remove
 * "one time" metrics after produced (i.e. once a base generation is complete
 * then remove metric so it doesn't keep repeating needlessly).
 * Another possible option is to simple perform the reporter.start()
 * when all the processing is complete and the app is preparing to end.
 */
public final class MetricMonitor
{
	private static final Logger LOG = Logger.getLogger(MetricMonitor.class.getName());

	/**
	 *  Metrics data registry (drop wizard)
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final MetricRegistry metrics = new MetricRegistry();

	/**
	 * map from some framework identity type to a timer.
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final Map<OutputableIntfc, Timer> timers = new ConcurrentHashMap<>();

	/**
	 * Empty constructor
	 */
	private MetricMonitor()
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
		LOG.info("Metrics Enabled");
//		final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
//                .outputTo(LoggerFactory.getLogger("com.starcases.prime.metrics"))
//                .convertRatesTo(TimeUnit.SECONDS)
//                .convertDurationsTo(TimeUnit.MILLISECONDS)
//                .build();

		final ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
		          .convertRatesTo(TimeUnit.SECONDS)
		          .convertDurationsTo(TimeUnit.MILLISECONDS)
		          .build();

		reporter.start(15, TimeUnit.SECONDS);
	}

}
