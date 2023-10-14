module com.starcases.prime.metrics.api
{
	exports com.starcases.prime.metrics.api;

	requires transitive com.starcases.prime.core.api;
	requires transitive com.starcases.prime.service.api;

	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
}