module com.starcases.prime.core.impl
{
	exports com.starcases.prime.base.impl;
	exports com.starcases.prime.core.impl;
	exports com.starcases.prime.datamgmt.impl;

	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.metrics.impl;
	requires transitive com.starcases.prime.preload.api;
	requires com.starcases.prime.service.impl;
	requires jakarta.validation;
	requires java.logging;
	requires lombok;
	requires micrometer.core;
	requires org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	provides com.starcases.prime.base.api.BaseTypesProviderIntfc with com.starcases.prime.base.impl.BuiltinBaseTypesProvider;
	provides com.starcases.prime.datamgmt.api.CollectionTrackerProviderIntfc with com.starcases.prime.datamgmt.impl.CollectionTrackerProvider;
}