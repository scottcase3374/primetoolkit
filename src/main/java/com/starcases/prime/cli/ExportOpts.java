package com.starcases.prime.cli;

import picocli.CommandLine.Option;

class ExportOpts
{
	@Option(names = {"-e", "--export"},arity="0..1", description = "Valid vals: ${COMPLETION-CANDIDATES}")
	Export exportType;
}