module com.starcases.prime {
	exports com.starcases.prime;
	exports com.starcases.prime.cli;

	requires com.starcases.prime.preload.api;
	requires com.starcases.prime.service.api;
	requires com.starcases.prime.sql.api;


	requires com.starcases.prime.base.nprime.impl;
	requires com.starcases.prime.base.prefix.impl;
	requires com.starcases.prime.base.primetree;
	requires com.starcases.prime.base.triples;

	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.core.impl;

	requires com.starcases.prime.graph.export.api;
	requires com.starcases.prime.graph.impl;
	requires com.starcases.prime.graph.visualize.impl;

	requires com.starcases.prime.cache;

	//requires infinispan.core;
	requires info.picocli;
	requires jakarta.validation;
	requires java.desktop;
	requires java.logging;
	requires static lombok;
	requires org.antlr.antlr4.runtime;
	requires org.eclipse.collections.api;
	requires org.eclipse.collections.impl;
	requires org.jgrapht.core;
	//requires org.junit.jupiter.api;
}