open module com.starcases.prime.preload.api {
	exports com.starcases.prime.preload.api;

	requires cache.api;
	requires com.starcases.prime.service.api;
	requires infinispan.core;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires static protostream;
	requires protostream.processor;
	requires protostream.types;
}