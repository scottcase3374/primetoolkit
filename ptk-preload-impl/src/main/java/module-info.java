module com.starcases.prime.preload.impl {
	exports com.starcases.prime.preload.impl;

	requires transitive com.starcases.prime.preload.api;
	requires com.starcases.prime.core.impl;

	requires infinispan.core;
	//requires infinispan.commons;
	requires java.compiler;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires protostream;
	requires protostream.processor;
	requires protostream.types;

}