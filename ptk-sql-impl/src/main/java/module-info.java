module com.starcases.prime.sql.impl
{
	exports com.starcases.prime.sql.impl;
	exports com.starcases.prime.sql.antlrimpl;


	requires com.google.gson;
	requires transitive com.starcases.prime.core.api;
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
}
