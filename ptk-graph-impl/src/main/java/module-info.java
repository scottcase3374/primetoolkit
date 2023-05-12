module com.starcases.prime.graph.impl {
	exports com.starcases.prime.graph.impl;
	exports com.starcases.prime.graph.log;
	exports com.starcases.prime.log;

	requires transitive com.starcases.prime.core.api;

	requires info.picocli;
	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires transitive org.jgrapht.core;
	requires com.starcases.prime.core.impl;


}