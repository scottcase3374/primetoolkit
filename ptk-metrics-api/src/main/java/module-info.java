open module com.starcases.prime.metrics.api
{
	exports com.starcases.prime.metrics.api;
	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.service.api;
	requires java.logging;
	requires static lombok;
	//requires micrometer.core;
	requires transitive org.eclipse.collections.api;
}