module com.starcases.prime.core.api
{
	exports com.starcases.prime.common.api;
	exports com.starcases.prime.base.api;
	exports com.starcases.prime.core.api;
	exports com.starcases.prime.datamgmt.api;

	requires transitive com.starcases.prime.kern.api;
	requires com.starcases.prime.service.api;

	requires jakarta.validation;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires org.eclipse.collections.impl;
}