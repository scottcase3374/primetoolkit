module com.starcases.prime.base.triples.impl
{
	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;
	requires com.starcases.prime.logging;
	requires com.starcases.prime.service.impl;

	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	provides com.starcases.prime.base.api.BaseTypesProviderIntfc with com.starcases.prime.base.triples.impl.TripleBaseTypeProvider;
	provides com.starcases.prime.base.api.BaseProviderIntfc with com.starcases.prime.base.triples.impl.TripleProvider;
	provides com.starcases.prime.base.api.LogPrimeDataProviderIntfc with com.starcases.prime.base.triples.impl.LogTriplesDataProvider;
}