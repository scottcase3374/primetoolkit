module com.starcases.prime.cli
{
	exports com.starcases.prime.cli;

	requires com.starcases.prime.cache.api;
	requires com.starcases.prime.cache.impl;
	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;
	requires com.starcases.prime.error.api;
	requires com.starcases.prime.graph.export.api;
	requires com.starcases.prime.logging;
	requires com.starcases.prime.metrics.api;
	requires com.starcases.prime.service.impl;
	requires com.starcases.prime.sql.api;

	requires cache.api;
	requires info.picocli;
	requires jakarta.validation;
	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;
	requires org.jgrapht.core;

	uses com.starcases.prime.base.api.BaseTypesIntfc;
	uses com.starcases.prime.base.api.BaseTypesProviderIntfc;
	uses com.starcases.prime.cache.api.CacheProviderIntfc;
	uses com.starcases.prime.cache.api.persistload.PersistLoaderProviderIntfc;
	uses com.starcases.prime.cache.api.preload.PreloaderProviderIntfc;
	uses com.starcases.prime.cache.api.subset.PrimeSubsetProviderIntfc;
	uses com.starcases.prime.error.api.PtkErrorHandlerIntfc;
	uses com.starcases.prime.graph.export.api.ExportsProviderIntfc;
	uses com.starcases.prime.metrics.api.MetricProviderIntfc;
	uses com.starcases.prime.sql.api.SqlProviderIntfc;
}