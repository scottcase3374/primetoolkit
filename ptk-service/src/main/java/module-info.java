open module com.starcases.prime.service
{
	exports com.starcases.prime.service;
	exports com.starcases.prime.service.api;

	requires transitive java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;

	requires com.starcases.prime.core.api;

	uses com.starcases.prime.base.api.BaseTypesProviderIntfc;
}