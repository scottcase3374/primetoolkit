package com.starcases.prime.cli;

import picocli.CommandLine.Option;

class LogOpts
{
	@Option(names = {"--log","-l"}, arity = "0..1", description = "Valid vals: ${COMPLETION-CANDIDATES}")
	LogOper logOper;
}