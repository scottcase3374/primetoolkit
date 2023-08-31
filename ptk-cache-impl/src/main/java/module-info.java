module com.starcases.prime.cache.impl
{
	exports com.starcases.prime.cache.impl.prime;

	requires transitive com.starcases.prime.cache.api;
	requires com.starcases.prime.kern.api;
	requires com.starcases.prime.kern.impl;
	requires com.starcases.prime.service.impl;
	requires com.starcases.prime.service.api;
	requires transitive com.starcases.prime.core.api;

	requires transitive cache.api;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires com.beanit.asn1bean;

	provides com.starcases.prime.cache.api.CacheProviderIntfc with com.starcases.prime.cache.impl.CacheProvider;

	provides com.starcases.prime.cache.api.persistload.PersistPrefixLoaderProviderIntfc with com.starcases.prime.cache.impl.prefixes.PersistedPrefixLoaderProvider;
	provides com.starcases.prime.cache.api.persistload.PersistLoaderProviderIntfc with com.starcases.prime.cache.impl.prime.PersistedPrimesLoaderProvider;

	provides com.starcases.prime.cache.api.primetext.PrimeTextFileLoaderProviderIntfc with com.starcases.prime.cache.impl.primetext.PrimeTextFileLoaderProvider;
	provides com.starcases.prime.cache.api.subset.PrimeSubsetProviderIntfc with com.starcases.prime.cache.impl.subset.PrimeSubsetProvider;

	uses com.starcases.prime.kern.api.StatusHandlerIntfc;
}

