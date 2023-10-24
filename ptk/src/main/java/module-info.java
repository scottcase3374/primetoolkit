open module PrimeToolKit
{
	exports com.starcases.prime;

	requires com.starcases.prime.cli;
	requires com.starcases.prime.sql.jsonoutput.impl;

	requires info.picocli;
	requires static picocli.codegen;
	requires java.logging;
	requires static lombok;
}