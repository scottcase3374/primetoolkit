module com.starcases.prime.base.primetree {
	exports com.starcases.prime.base.primetree.impl;

	requires com.starcases.prime.service.api;
	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;
	//requires com.starcases.prime.metrics.impl;

	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	requires java.logging;
	requires micrometer.core;

	requires static lombok;

	provides com.starcases.prime.base.api.BaseProviderIntfc with com.starcases.prime.base.primetree.impl.PrimeTreeProvider;
}