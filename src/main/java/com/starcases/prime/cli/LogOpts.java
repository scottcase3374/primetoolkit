package com.starcases.prime.cli;

import picocli.CommandLine.Option;

/**
 *
 * Command line interface setup
 *
 */
class LogOpts
{
	@Option(names = {"--log","-l"}, arity = "0..2", description = "Valid vals: ${COMPLETION-CANDIDATES}")
	LogOper logOper;
}