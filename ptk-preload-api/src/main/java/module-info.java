open module com.starcases.prime.preload.api {
	exports com.starcases.prime.preload.api;

	requires com.starcases.prime.service.api;
	requires infinispan.core;
	//requires infinispan.commons;
	requires java.compiler;
	requires static lombok;
	requires org.eclipse.collections.api;
	requires protostream;
	requires protostream.processor;
	requires protostream.types;
}