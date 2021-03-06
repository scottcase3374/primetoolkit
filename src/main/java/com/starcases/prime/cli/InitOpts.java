package com.starcases.prime.cli;

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
	/**
	 * maximum number of primes to manage
	 */
	@Getter
	@Setter
	@Option(names = {"-m", "--max-count"}, paramLabel = "COUNT", description = "Max count of primes to process", defaultValue = "1500", required = false)
	private int maxCount;

	/**
	 * Confidence level for the "cheating" prime determination check
	 */
	@Getter
	@Setter
	@Option(names = {"--confidence-level"}, description = "Drives confidence level of primality check.", defaultValue = "100", required = false)
	private int confidenceLevel;

	/**
	 * Log level
	 */
	@Getter
	@Setter
	@Option(names = { "--log-level"}, description = "Valid vals: ${COMPLETION-CANDIDATES}", defaultValue="INFO", required = false)
	private Level logLevel;

	/**
	 * Folder for all output
	 */
	@Getter
	@Setter
	@Option(names = {"--output-folder"}, description = "Base folder for all files generated", defaultValue = "~/ptk-output", required = true)
	private String outputFolder;

	/**
	 * flag indicating whether stdout should redirect to a file
	 */
	@Getter
	@Setter
	@Option(names = {"--stdout-redirect"}, description = "Indicate stdout should redirect to a file", required = false)
	private boolean stdOuputRedir;

	/**
	 * flag indicating if use of multiple CPU cores is ok
	 */
	@Getter
	@Setter
	@Option(names = {"--prefer-parallel"}, description = "Prefer parallel streams when possible", defaultValue = "true", required = false)
	private boolean preferParallel;

	/**
	 * flag indicating whether to cache primes.  Unused right now.
	 */
	@Getter
	@Setter
	@Option(names = {"--store-primes"}, arity = "0..4", description = "Store types: ${COMPLETION-CANDIDATES}", required = false)
	private BaseTypes storePrimes;

	/**
	 * flag indicating whether to load cached primes - unused right now.
	 */
	@Getter
	@Setter
	@Option(names = {"--load-primes"}, arity = "0..4", description = "Load types: ${COMPLETION-CANDIDATES}", required = false)
	private BaseTypes loadPrimes;
}