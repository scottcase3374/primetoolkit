module com.starcases.prime.base.prefix.impl
{
	requires transitive com.starcases.prime.cache.api;
	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;
	requires transitive com.starcases.prime.kern.api;
	requires com.starcases.prime.logging;
	requires com.starcases.prime.service.impl;

	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;

	provides com.starcases.prime.base.api.BaseTypesProviderIntfc with com.starcases.prime.base.prefix.impl.PrefixBaseTypeProvider;
	provides com.starcases.prime.base.api.BaseProviderIntfc with com.starcases.prime.base.prefix.impl.PrefixProvider;
	provides com.starcases.prime.base.api.LogPrimeDataProviderIntfc with com.starcases.prime.base.prefix.impl.LogPrefixDataProvider;

	uses com.starcases.prime.kern.api.StatusHandlerIntfc;
}