module com.starcases.prime.base.primetree.impl
{
	exports com.starcases.prime.base.primetree.impl;

	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;
	requires com.starcases.prime.logging;
	requires com.starcases.prime.service.impl;
	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	provides com.starcases.prime.base.api.BaseProviderIntfc with com.starcases.prime.base.primetree.impl.PrimeTreeProvider;
}