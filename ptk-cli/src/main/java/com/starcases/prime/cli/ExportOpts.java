package com.starcases.prime.cli;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

/**
 *
 * Command line interface setups
 *
 */
class ExportOpts
{
	/**
	 * export type to generate - picocli
	 */
	@Getter
	@Setter
	@Option(names = {"-e", "--export"},arity="0..1", description = "Valid vals: ${COMPLETION-CANDIDATES}")
	private Export exportType;
}