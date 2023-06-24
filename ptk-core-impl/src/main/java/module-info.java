module com.starcases.prime.core.impl
{
	exports com.starcases.prime.base.impl;
	exports com.starcases.prime.core.impl;
	exports com.starcases.prime.datamgmt.impl;

	requires transitive com.starcases.prime.cache.api;
	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.error.api;
	requires transitive com.starcases.prime.metrics.api;
	requires com.starcases.prime.service.impl;

	requires jakarta.validation;
	requires java.logging;
	requires static lombok;
	requires micrometer.core;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	provides com.starcases.prime.base.api.BaseTypesProviderIntfc with com.starcases.prime.base.impl.BaseTypesGlobalProvider;
	provides com.starcases.prime.base.api.BaseGenDecorProviderIntfc with com.starcases.prime.base.impl.BaseGenTimerMetricDecorProvider;
	provides com.starcases.prime.datamgmt.api.CollectionTrackerProviderIntfc with com.starcases.prime.datamgmt.impl.CollectionTrackerProvider;

	uses com.starcases.prime.metrics.api.MetricProviderIntfc;
}