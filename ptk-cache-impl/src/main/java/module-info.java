module com.starcases.prime.cache.impl {
	exports com.starcases.prime.cache.impl;

	requires transitive com.starcases.prime.cache;
	requires com.starcases.prime.core.impl;

	requires infinispan.core;
	//requires infinispan.commons;
	requires static lombok;
	requires transitive org.eclipse.collections.api;

}