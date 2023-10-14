module com.starcases.prime.sql.api
{
	exports com.starcases.prime.sql.api;

	requires transitive com.starcases.prime.core.api;
	requires transitive com.starcases.prime.service.api;

	requires static lombok;
	requires transitive org.eclipse.collections.api;
}