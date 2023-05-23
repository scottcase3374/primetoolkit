module com.starcases.prime.graph.visualize.impl
{
	exports com.starcases.prime.graph.visualize.impl;

	requires com.github.vlsi.mxgraph.jgraphx;
	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.graph.impl;
	requires transitive com.starcases.prime.graph.visualize.api;
	requires com.starcases.prime.service.impl;
	requires java.desktop;
	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.jgrapht.core;
	requires org.jgrapht.ext;
}