module com.starcases.prime.core.impl
{
	exports com.starcases.prime.base.impl;
	exports com.starcases.prime.core.impl;
	exports com.starcases.prime.datamgmt.impl;

	requires transitive com.starcases.prime.core.api;
	requires transitive com.starcases.prime.metrics.api;
	requires transitive com.starcases.prime.preload.api;
	requires com.starcases.prime.service.impl;

	requires jakarta.validation;
	requires java.logging;
	requires lombok;
	requires micrometer.core;
	requires org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	provides com.starcases.prime.base.api.BaseTypesProviderIntfc with com.starcases.prime.base.impl.BaseTypesGlobalProvider;
	provides com.starcases.prime.base.api.BaseGenDecorProviderIntfc with com.starcases.prime.base.impl.BaseGenTimerMetricDecorProvider;
	provides com.starcases.prime.datamgmt.api.CollectionTrackerProviderIntfc with com.starcases.prime.datamgmt.impl.CollectionTrackerProvider;

	uses com.starcases.prime.metrics.api.MetricProviderIntfc;
}