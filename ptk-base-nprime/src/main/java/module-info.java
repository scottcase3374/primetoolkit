module com.starcases.prime.base.nprime.impl
{
	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;
	requires transitive com.starcases.prime.kern.api;
	requires com.starcases.prime.logging;
	requires transitive com.starcases.prime.service.api;
	requires com.starcases.prime.service.impl;

	requires jakarta.validation;
	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	provides com.starcases.prime.base.api.BaseTypesProviderIntfc with com.starcases.prime.base.nprime.impl.NPrimeBaseTypeProvider;
	provides com.starcases.prime.base.api.BaseProviderIntfc with com.starcases.prime.base.nprime.impl.NPrimeProvider;
	provides com.starcases.prime.base.api.LogPrimeDataProviderIntfc with com.starcases.prime.base.nprime.impl.LogNPrimeDataProvider;

	uses com.starcases.prime.kern.api.StatusHandlerIntfc;
}