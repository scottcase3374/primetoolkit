module com.starcases.prime.cache.impl {
	exports com.starcases.prime.cache.impl;

	requires cache.api;
	requires transitive com.starcases.prime.cache.api;
	requires static lombok;
	requires transitive org.eclipse.collections.api;

	//provides com.starcases.prime.cache.api.CacheProviderIntfc with com.starcases.prime.cache.impl.CacheProvider;
}