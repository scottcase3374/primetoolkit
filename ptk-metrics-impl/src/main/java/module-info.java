module com.starcases.prime.metrics.impl {
	exports com.starcases.prime.metrics.impl;

	requires transitive com.starcases.prime.metrics.api;
	requires com.starcases.prime.service.api;
	requires com.starcases.prime.core.api;
	requires static lombok;
	requires java.logging;
	requires micrometer.core;
	requires micrometer.registry.graphite;
	requires transitive org.eclipse.collections.api;
}