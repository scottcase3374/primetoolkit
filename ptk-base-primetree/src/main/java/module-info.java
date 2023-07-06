module com.starcases.prime.base.primetree.impl
{
	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;
	requires transitive com.starcases.prime.kern.api;
	requires com.starcases.prime.logging;
	requires com.starcases.prime.service.impl;

	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	provides com.starcases.prime.base.api.BaseTypesProviderIntfc with com.starcases.prime.base.primetree.impl.PrimeTreeBaseTypeProvider;
	provides com.starcases.prime.base.api.BaseProviderIntfc with com.starcases.prime.base.primetree.impl.PrimeTreeProvider;
	provides com.starcases.prime.base.api.LogPrimeDataProviderIntfc with com.starcases.prime.base.primetree.impl.LogPrimeTreeDataProvider;

	uses com.starcases.prime.kern.api.StatusHandlerIntfc;
}