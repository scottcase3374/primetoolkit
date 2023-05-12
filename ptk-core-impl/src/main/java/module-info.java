module com.starcases.prime.core.impl {

	exports com.starcases.prime.core.impl;
	exports com.starcases.prime.base.impl;
	exports com.starcases.prime.datamgmt.impl;
	exports com.starcases.prime.logging;

	requires transitive com.starcases.prime.preload.api;
	requires transitive com.starcases.prime.core.api;

	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;
	requires jakarta.validation;
	requires micrometer.core;
}