// uses open due to gson
open module com.starcases.prime.sql.jsonoutput.impl
{
	requires transitive com.starcases.prime.core.api;
	requires transitive com.starcases.prime.kern.api;
	requires com.starcases.prime.service.impl;
	requires transitive com.starcases.prime.sql.api;

	requires com.google.gson;

	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;

	provides com.starcases.prime.sql.api.OutputProviderIntfc with com.starcases.prime.sql.jsonoutput.impl.JSONOutputProvider;

	uses com.starcases.prime.base.api.BaseTypesProviderIntfc;
}
