module com.starcases.prime.kern.impl
{
	exports com.starcases.prime.kern.impl;

	requires transitive com.starcases.prime.kern.api;
	requires com.starcases.prime.service.api;

	requires static lombok;
	requires transitive org.eclipse.collections.impl;
	requires java.logging;

	provides com.starcases.prime.kern.api.StatusHandlerProviderIntfc with com.starcases.prime.kern.impl.StatusHandlerProvider;
}