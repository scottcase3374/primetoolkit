package com.starcases.prime.cli;

import com.starcases.prime.base.BaseTypes;

import picocli.CommandLine.Option;

/**
 *
 * Command Line Interface setups
 *
 */
class BaseOpts
{
	@Option(names = {"-b", "--base"}, arity = "0..1", description = "Valid vals: ${COMPLETION-CANDIDATES}")
	BaseTypes bases;

	@Option(names = {"--max-reduce"}, description = "Max bases [0-max) to use for NPrime.", defaultValue="2", required = false)
	int maxReduce;

	@Option(names = {"--active-base-id"}, description = "Valid vals: ${COMPLETION-CANDIDATES}", defaultValue = "DEFAULT", required = false)
	BaseTypes activeBaseId;

	@Option(names = {"--log-generate"}, description = "Log generation of base during creation", defaultValue = "false", required = false)
	boolean logGenerate;
}