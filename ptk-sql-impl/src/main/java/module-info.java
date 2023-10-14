// uses open due to gson
open module com.starcases.prime.sql.impl
{
	requires transitive com.starcases.prime.cache.api;
	requires transitive com.starcases.prime.core.api;
	requires transitive com.starcases.prime.kern.api;
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
	//requires org.eclipse.collections.impl;
	requires com.opencsv;

	provides com.starcases.prime.sql.api.SqlProviderIntfc with com.starcases.prime.sql.impl.SqlCmdSvrProvider;
	provides com.starcases.prime.sql.api.OutputServiceIntfc with com.starcases.prime.sql.jsonoutput.JSONOutputServiceImpl;
	//provides com.starcases.prime.sql.impl.OutputServiceIntfc with com.starcases.prime.sql.csvoutput.CSVOutputSvcImpl;

	uses com.starcases.prime.base.api.BaseTypesProviderIntfc;
	uses com.starcases.prime.kern.api.StatusHandlerIntfc;
	//uses com.starcases.prime.sql.impl.OutputServiceIntfc;
}
