package com.starcases.prime.cli;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

/**
 *
 * Command line interface setups
 *
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
class ExportOpts
{
	@Getter
	@Setter
	@Option(names = {"-e", "--export"},arity="0..1", description = "Valid vals: ${COMPLETION-CANDIDATES}")
	private Export exportType;

	@Getter
	@Setter
	@Option(names = { "--export-file"}, description = "Path/file to export file.", required = false)
	private String exportFile;
}