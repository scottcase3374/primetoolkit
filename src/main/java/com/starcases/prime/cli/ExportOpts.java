package com.starcases.prime.cli;

import picocli.CommandLine.Option;

/**
 *
 * Command line interface setups
 *
 */
class ExportOpts
{
	@Option(names = {"-e", "--export"},arity="0..1", description = "Valid vals: ${COMPLETION-CANDIDATES}")
	Export exportType;

	@Option(names = { "--export-file"}, description = "Path/file to export file.", required = false)
	String exportFile;
}