module com.starcases.prime.service.impl
{
	exports com.starcases.prime.service.impl;

	requires transitive com.starcases.prime.service.api;

	requires static lombok;
	requires org.eclipse.collections.api;

}