module com.starcases.prime.graph.export.gml {
	exports com.starcases.prime.graph.export.gml;

	requires transitive com.starcases.prime.graph.export.api;
	requires transitive com.starcases.prime.core.api;

	requires jakarta.validation;
	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;

}