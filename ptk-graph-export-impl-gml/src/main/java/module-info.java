module com.starcases.graph.export.impl.gml {
	exports com.starcases.prime.graph.export.impl.gml;

	requires transitive com.starcases.prime.core.api;
	requires transitive com.starcases.prime.graph.export.api;

	requires com.starcases.prime.service.impl;

	requires jakarta.validation;
	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;

	provides com.starcases.prime.graph.export.api.ExportsProviderIntfc with com.starcases.prime.graph.export.impl.gml.ExportGMLProvider;
}