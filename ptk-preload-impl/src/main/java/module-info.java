module com.starcases.prime.preload.impl
{
	exports com.starcases.prime.preload.impl;

	requires cache.api;
	requires com.starcases.prime.core.impl;
	requires transitive com.starcases.prime.preload.api;
	requires transitive com.starcases.prime.service.impl;
	requires java.compiler;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires protostream;
	requires protostream.types;

	provides com.starcases.prime.preload.api.PreloaderProviderIntfc with com.starcases.prime.preload.impl.PreloaderProvider;
}