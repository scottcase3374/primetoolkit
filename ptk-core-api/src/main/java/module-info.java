module com.starcases.prime.core.api
{
	exports com.starcases.prime.common.api;
	exports com.starcases.prime.base.api;
	exports com.starcases.prime.core.api;
	exports com.starcases.prime.datamgmt.api;

	requires com.starcases.prime.service.impl;

	requires jakarta.validation;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;
}