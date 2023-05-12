open module com.starcases.prime.sql.api {
	exports com.starcases.prime.sql.api;

	requires com.starcases.prime.service.api;
	requires transitive com.starcases.prime.core.api;
	requires static lombok;
}