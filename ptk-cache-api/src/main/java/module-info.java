module com.starcases.prime.cache.api
{
	exports com.starcases.prime.cache.api;

	requires com.starcases.prime.service.api;

	requires transitive cache.api;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
}