open module com.starcases.prime.graph.visualize.api {
	exports com.starcases.prime.graph.visualize.api;

	requires transitive com.starcases.prime.core.api;
	requires transitive java.desktop;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires transitive org.jgrapht.core;
	requires com.starcases.prime.service.api;
}