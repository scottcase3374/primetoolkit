module com.starcases.prime.error.impl
{
	exports com.starcases.prime.error.impl;

	requires transitive com.starcases.prime.error.api;

	requires transitive java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;
}