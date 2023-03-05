package com.starcases.prime.cli;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

/**
 *
 * Command line interface setup
 *
 */
class MetricsOpts
{
	public enum MetricOpt
	{
		JVM_MEMORY_METRIC,
		JVM_GC_METRIC,
		PROCESSOR_METRIC,
		JVM_THREAD_METRIC,
		LOGGING_REGISTRY,
		SIMPLE_REGISTRY,
		GRAPHITE_REGISTRY,

		/**
		 * all metrics and registries.
		 */
		ALL
		;
	}

	/**
	 * Metrics to manage - for micrometer
	 */
	@Getter
	@Setter
	@Option(names = {"--metrics"}, arity="0..10", description = "Valid vals: ${COMPLETION-CANDIDATES}" )
	private MetricOpt[] metricType;
}