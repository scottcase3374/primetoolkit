module com.starcases.prime.sql.impl
{
	exports com.starcases.prime.sql.impl;
	exports com.starcases.prime.sql.antlrimpl;

	requires transitive com.starcases.prime.core.api;
	requires com.starcases.prime.error.api;
	requires com.starcases.prime.service.impl;
	requires transitive com.starcases.prime.sql.api;

	requires com.google.gson;
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

	provides com.starcases.prime.sql.api.SqlProviderIntfc with com.starcases.prime.sql.impl.SqlCmdSvrProvider;

	uses com.starcases.prime.base.api.BaseTypesProviderIntfc;
}
