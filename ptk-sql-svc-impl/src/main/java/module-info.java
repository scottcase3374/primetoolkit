module com.starcases.prime.sql.impl {
	exports com.starcases.prime.sql.impl;
	exports com.starcases.prime.sql.antlr;

	requires com.google.gson;
	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.service.api;
	requires transitive com.starcases.prime.sql.api;
	requires io.netty.buffer;
	requires io.netty.codec;
	requires io.netty.codec.http;
	requires io.netty.common;
	requires io.netty.transport;
	requires java.logging;
	requires static lombok;
	requires org.antlr.antlr4.runtime;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;

	//requires com.starcases.prime.metrics.impl;
	//requires micrometer.core;
}