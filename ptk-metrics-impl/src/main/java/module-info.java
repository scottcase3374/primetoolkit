module com.starcases.prime.metrics.impl
{
	exports com.starcases.prime.metrics.impl;

	requires transitive com.starcases.prime.core.api;
	requires transitive com.starcases.prime.metrics.api;
	requires com.starcases.prime.service.api;
	requires com.starcases.prime.service.impl;
	requires java.logging;
	requires static lombok;
	requires micrometer.core;
	requires micrometer.registry.graphite;
	requires transitive org.eclipse.collections.api;
}