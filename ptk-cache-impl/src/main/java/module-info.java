module com.starcases.prime.cache.impl
{
	exports com.starcases.prime.cache.impl;

	requires transitive com.starcases.prime.cache.api;
	requires com.starcases.prime.kern.api;
	requires com.starcases.prime.service.impl;
	requires com.starcases.prime.service.api;
	requires transitive com.starcases.prime.core.api;

	requires transitive cache.api;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires com.beanit.asn1bean;

	provides com.starcases.prime.cache.api.CacheProviderIntfc with com.starcases.prime.cache.impl.CacheProvider;
	provides com.starcases.prime.cache.api.persistload.PersistLoaderProviderIntfc with com.starcases.prime.cache.impl.persistload.PersistLoaderProvider;
	provides com.starcases.prime.cache.api.preload.PreloaderProviderIntfc with com.starcases.prime.cache.impl.preload.PreloaderProvider;
	provides com.starcases.prime.cache.api.subset.PrimeSubsetProviderIntfc with com.starcases.prime.cache.impl.subset.PrimeSubsetProvider;

	uses com.starcases.prime.kern.api.StatusHandlerIntfc;
}

