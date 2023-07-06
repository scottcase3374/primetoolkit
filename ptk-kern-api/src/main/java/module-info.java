module com.starcases.prime.kern.api
{
	exports com.starcases.prime.kern.api;

	requires com.starcases.prime.service.api;

	requires transitive java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.impl;
	requires org.eclipse.collections.api;

	uses com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
}