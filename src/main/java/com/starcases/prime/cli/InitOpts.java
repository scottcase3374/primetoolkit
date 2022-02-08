package com.starcases.prime.cli;

import java.io.File;
import java.util.logging.Level;

import picocli.CommandLine.Option;

/**
 *
 * command line interface setup
 *
 */
class InitOpts
{
	@Option(names = {"-m", "--max-count"}, paramLabel = "COUNT", description = "Max count of primes to process", defaultValue = "1500", required = false)
	int maxCount;

	@Option(names = {"--confidence-level"}, description = "Drives confidence level of primality check.", defaultValue = "100", required = false)
	int confidenceLevel;

	@Option(names = { "--log-level"}, description = "Valid vals: ${COMPLETION-CANDIDATES}", defaultValue="INFO", required = false)
	Level logLevel;

	@Option(names = { "--log-file"}, description = "Path/file for logger output", required = false)
	String logFile;

	@Option(names = { "--output-file"}, description = "Path/file for non-logger output (i.e. std-out / console)", required = false)
	File outputFile;

	@Option(names = {"--prefer-parallel"}, description = "Prefer parallel streams when possible", defaultValue = "true", required = false)
	boolean preferParallel;
}