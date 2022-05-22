package com.starcases.prime.cli;

import java.io.File;
import java.util.logging.Level;

import com.starcases.prime.base.BaseTypes;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

/**
 *
 * command line interface setup
 *
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
@Setter
@Getter
class InitOpts
{
	@Getter
	@Setter
	@Option(names = {"-m", "--max-count"}, paramLabel = "COUNT", description = "Max count of primes to process", defaultValue = "1500", required = false)
	private int maxCount;

	@Getter
	@Setter
	@Option(names = {"--confidence-level"}, description = "Drives confidence level of primality check.", defaultValue = "100", required = false)
	private int confidenceLevel;

	@Getter
	@Setter
	@Option(names = { "--log-level"}, description = "Valid vals: ${COMPLETION-CANDIDATES}", defaultValue="INFO", required = false)
	private Level logLevel;

	@Getter
	@Setter
	@Option(names = { "--log-file"}, description = "Path/file for logger output", required = false)
	private String logFile;

	@Getter
	@Setter
	@Option(names = {"--output-folder"}, description = "Base folder for all files generated", defaultValue = "~/ptk-output", required = true)
	private File outputFolder;

	@Getter
	@Setter
	@Option(names = {"--prefer-parallel"}, description = "Prefer parallel streams when possible", defaultValue = "true", required = false)
	private boolean preferParallel;

	@Getter
	@Setter
	@Option(names = {"--store-primes"}, arity = "0..4", description = "Store types: ${COMPLETION-CANDIDATES}", required = false)
	private BaseTypes storePrimes;

	@Getter
	@Setter
	@Option(names = {"--load-primes"}, arity = "0..4", description = "Load types: ${COMPLETION-CANDIDATES}", required = false)
	private BaseTypes loadPrimes;
}