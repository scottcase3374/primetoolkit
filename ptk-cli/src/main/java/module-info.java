open module com.starcases.prime.cli
{
	exports com.starcases.prime.cli;

	requires cache.api;
	requires com.starcases.prime.cache.api;
	requires com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;
	requires com.starcases.prime.graph.export.api;
	requires com.starcases.prime.logging;
	requires com.starcases.prime.metrics.api;
	requires com.starcases.prime.preload.api;
	requires com.starcases.prime.service;
	requires com.starcases.prime.sql.api;
	requires info.picocli;
	requires jakarta.validation;
	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;
	requires org.jgrapht.core;

	//uses com.starcases.prime.base.api.BaseTypesProviderIntfc;
}