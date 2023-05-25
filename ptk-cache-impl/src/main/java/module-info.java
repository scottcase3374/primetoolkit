module com.starcases.prime.cache.impl {
	exports com.starcases.prime.cache.impl;

	requires transitive cache.api;
	requires transitive com.starcases.prime.cache.api;
	requires com.starcases.prime.service.api;
	requires transitive java.transaction.xa;
	requires static lombok;
	requires transitive org.eclipse.collections.api;

	//requires java.transaction;
	//requires infinispan.core;
	//provides com.starcases.prime.cache.api.CacheProviderIntfc with com.starcases.prime.cache.impl.CacheProvider;
}

