module com.starcases.prime.error.impl
{
	requires transitive com.starcases.prime.error.api;
	requires com.starcases.prime.service.api;

	requires transitive java.logging;
	requires static lombok;
	requires transitive org.eclipse.collections.api;

	provides com.starcases.prime.error.api.PtkErrorHandlerProviderIntfc with com.starcases.prime.error.impl.PtkErrorHandlerProvider;
}