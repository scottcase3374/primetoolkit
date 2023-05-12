module com.starcases.prime.base.nprime.impl {
	exports com.starcases.prime.base.nprime.impl;

	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.service.api;

	//requires com.starcases.prime.metrics.impl;
	requires com.starcases.prime.core.impl;

	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;
	requires micrometer.core;
	requires info.picocli;
	requires jakarta.validation;
	requires java.logging;

	requires static lombok;

	provides com.starcases.prime.base.api.BaseProviderIntfc with com.starcases.prime.base.nprime.impl.NPrimeProvider;
}