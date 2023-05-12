open module com.starcases.prime.cache {
	exports com.starcases.prime.cache.api;

	requires com.starcases.prime.service.api;

	requires infinispan.core;
	requires static lombok;
	requires transitive org.eclipse.collections.api;

}