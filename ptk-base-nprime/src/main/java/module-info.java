module com.starcases.prime.base.nprime.impl
{
	exports com.starcases.prime.base.nprime.impl;

	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;
	requires com.starcases.prime.logging;
	requires com.starcases.prime.service.impl;
	requires info.picocli;
	requires jakarta.validation;
	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	provides com.starcases.prime.base.api.BaseProviderIntfc with com.starcases.prime.base.nprime.impl.NPrimeProvider;
}