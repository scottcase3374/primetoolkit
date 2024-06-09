package com.starcases.prime.cli;

import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

/**
 *
 * command line interface setup
 *
 */
@Setter
@Getter
class InitOpts
{
	/**
	 * maximum prime idx to manage
	 */
	@Getter
	@Setter
	@Option(names = {"-m", "--max-idx"}, paramLabel = "INDEX", description = "Max idx of primes to process", defaultValue = "1500", required = false)
	private int maxIdx;

	/**
	 * maximum prime idx to manage
	 */
	@Getter
	@Setter
	@Option(names = {"-i", "--min-idx"}, paramLabel = "INDEX", description = "Min idx of primes to process", defaultValue = "0", required = false)
	private int minIdx;

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
	@Option(names = {"--output-folder"}, description = "Base folder for all files generated", defaultValue = "~/ptk/output", required = true)
	private String outputFolder;

	/**
	 * Folder input data
	 *
	 *  For the sample data included in this project; use something like:
	 *  	~/<eclipse-workspace-path>/PrimeToolKit/data
	 *
	 */
	@Getter
	@Setter
	@Option(names = {"--input-data-folder"}, description = "Base folder for preload of prime data", defaultValue = "~/ptk/input-data", required = true)
	private String inputDataFolder;

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
	 * flag indicating whether to clear previously cache primes.
	 */
	@Getter
	@Setter
	@Option(names = {"--clear-cached-primes"}, description = "Remove persisted cached primes [usually prep for fresh reload]", required = false)
	private boolean clearCachedPrimes;

	/**
	 * flag indicating whether to load cached primes.
	 */
	@Getter
	@Setter
	@Option(names = {"--load-primes"},  description = "Load and cache raw primes from files", required = false)
	private boolean loadPrimes;

	/**
	 * flag for enabling listening for remote commands based on a SQL like grammar.
	 */
	@Getter
	@Setter
	@Option(names = {"--enable-cmd-listener"}, description = "Listen for remote commands", required = false)
	private boolean enableCmmandListener;

	/**
	 * flag for setting port of command listener (which processes SQL-like commands, etc).
	 */
	@Getter
	@Setter
	@Option(names = {"--cmd-listener-port"}, description = "Port for the command (SQL) listener", defaultValue = "8690", required = false )
	private int cmdListenerPort;
}