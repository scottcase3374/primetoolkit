module com.starcases.prime.graph.export.api {

	exports com.starcases.prime.graph.export.api;

	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.service.api;

	requires static lombok;
	requires transitive org.eclipse.collections.api;
}