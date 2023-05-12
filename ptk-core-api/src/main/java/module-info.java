open module com.starcases.prime.core.api {

	exports com.starcases.prime.core.api;
	exports com.starcases.prime.base.api;
	exports com.starcases.prime.shared.api;
	exports com.starcases.prime.datamgmt.api;

	requires com.starcases.prime.service.api;
	requires transitive com.starcases.prime.preload.api;

	requires static lombok;
	requires transitive org.eclipse.collections.api;
	requires jakarta.validation;
	requires java.logging;

}