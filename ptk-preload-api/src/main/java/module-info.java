open module com.starcases.prime.preload.api {

	exports com.starcases.prime.preload.api;

	requires com.starcases.prime.service.api;

	requires cache.api;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
}