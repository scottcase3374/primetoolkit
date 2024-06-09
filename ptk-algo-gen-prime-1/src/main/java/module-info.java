module com.starcases.prime.algorithm.gen1.impl
{
	exports com.starcases.prime.algorithm.gen1.impl;

	requires transitive com.starcases.prime.cache.api;
	requires transitive com.starcases.prime.core.api;
	requires transitive com.starcases.prime.kern.api;
	requires transitive com.starcases.prime.metrics.api;
	requires com.starcases.prime.service.impl;

	requires jakarta.validation;
	requires java.logging;
	requires static lombok;
	requires micrometer.core;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;
	requires com.starcases.prime.kern.impl;

	provides com.starcases.prime.core.api.PrimeGenProviderIntfc with com.starcases.prime.algorithm.gen1.impl.PrimeGenProvider;

	uses com.starcases.prime.kern.api.StatusHandlerIntfc;
	uses com.starcases.prime.metrics.api.MetricProviderIntfc;
}