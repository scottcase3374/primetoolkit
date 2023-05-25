open module com.starcases.prime.cli
{
	exports com.starcases.prime.cli;

	requires cache.api;
	requires com.starcases.prime.cache.api;
	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;
	requires com.starcases.prime.graph.export.api;
	requires com.starcases.prime.logging;
	requires com.starcases.prime.metrics.api;
	requires com.starcases.prime.preload.api;
	requires com.starcases.prime.service.impl;
	requires com.starcases.prime.sql.api;
	requires info.picocli;
	requires jakarta.validation;
	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;
	requires org.jgrapht.core;
	requires java.transaction.xa;
	//requires	infinispan.core;
	//requires infinispan.commons;
	uses com.starcases.prime.sql.api.SqlProviderIntfc;
	uses com.starcases.prime.graph.export.api.ExportsProviderIntfc;
	uses com.starcases.prime.preload.api.PreloaderProviderIntfc;
	//uses com.starcases.prime.base.api.BaseTypesProviderIntfc;
	uses com.starcases.prime.base.api.BaseTypesIntfc;
}