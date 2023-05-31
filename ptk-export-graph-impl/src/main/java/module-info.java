module com.starcases.prime.graph.impl
{
	exports com.starcases.prime.graph.impl;

	requires transitive com.starcases.prime.core.api;

	requires java.logging;
	requires static lombok;
	requires transitive org.jgrapht.core;
}