module com.starcases.prime.base.prefix.impl {
	exports com.starcases.prime.base.prefix.impl;

	requires transitive com.starcases.prime.core.api;
	requires transitive com.starcases.prime.service.api;
	requires transitive com.starcases.prime.core.impl;
	//requires com.starcases.prime.metrics.impl;

	requires transitive org.eclipse.collections.api;

	requires java.logging;
	requires micrometer.core;

	requires static lombok;

	provides com.starcases.prime.base.api.BaseProviderIntfc with com.starcases.prime.base.prefix.impl.PrefixProvider;
}