module com.starcases.prime.preload.impl
{
	exports com.starcases.prime.preload.impl;

	requires transitive com.starcases.prime.core.api;
	requires transitive com.starcases.prime.preload.api;
	requires transitive com.starcases.prime.service.impl;

	requires com.beanit.asn1bean;
	requires cache.api;
	requires java.compiler;
	requires static lombok;
	requires transitive org.eclipse.collections.api;

	provides com.starcases.prime.preload.api.PreloaderProviderIntfc with com.starcases.prime.preload.impl.PreloaderProvider;
}