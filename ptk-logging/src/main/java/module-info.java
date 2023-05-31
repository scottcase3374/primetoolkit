module com.starcases.prime.logging
{
	exports com.starcases.prime.logging;

	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;
	requires com.starcases.prime.graph.impl;

	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.jgrapht.core;
}