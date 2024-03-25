module com.starcases.prime.cache.impl
{
	exports com.starcases.prime.cache.impl.prime;
	exports com.starcases.prime.cache.impl.prefixes;

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
	requires org.eclipse.collections.impl;

	//provides javax.cache.CacheManager 													with com.starcases.prime.cache.impl.prime.PrimeCacheProvider;

	provides com.starcases.prime.cache.api.persistload.PersistPrefixLoaderProviderIntfc with com.starcases.prime.cache.impl.prefixes.PersistedPrefixLoaderProvider;

	provides com.starcases.prime.cache.api.CacheProviderIntfc 					  with com.starcases.prime.cache.impl.prime.PrimeCacheProvider;

	provides com.starcases.prime.cache.api.CachePrefixProviderIntfc with com.starcases.prime.cache.impl.prefixes.PrefixCacheProvider;
	provides com.starcases.prime.cache.api.persistload.PersistLoaderProviderIntfc with com.starcases.prime.cache.impl.prime.PersistedPrimesLoaderProvider;
	provides com.starcases.prime.cache.api.subset.PrefixSubsetProviderIntfc with com.starcases.prime.cache.impl.subset.PrefixesSubsetProvider;
	provides com.starcases.prime.cache.api.primetext.PrimeTextFileLoaderProviderIntfc with com.starcases.prime.cache.impl.primetext.PrimeTextFileLoaderProvider;
	provides com.starcases.prime.cache.api.subset.PrimeSubsetProviderIntfc with com.starcases.prime.cache.impl.subset.PrimeSubsetProvider;
	//provides com.starcases.prime.cache.api.CachePrefixProviderIntfc with com.starcases.prime.cache.impl.prefixes.PrefixCacheProvider;
	uses com.starcases.prime.kern.api.StatusHandlerIntfc;
}

