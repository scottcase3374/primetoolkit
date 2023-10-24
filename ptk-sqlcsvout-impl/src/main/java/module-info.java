// uses open due to gson
open module com.starcases.prime.sql.csvoutput.impl
{
	requires transitive com.starcases.prime.core.api;
	requires transitive com.starcases.prime.kern.api;
	requires com.starcases.prime.service.impl;
	requires transitive com.starcases.prime.sql.api;

	requires java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.apache.commons.csv;

	provides com.starcases.prime.sql.api.OutputProviderIntfc with com.starcases.prime.sql.csvoutput.impl.CSVOutputProvider;

	uses com.starcases.prime.base.api.BaseTypesProviderIntfc;
}
