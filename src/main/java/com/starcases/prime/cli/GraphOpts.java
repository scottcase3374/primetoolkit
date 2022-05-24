package com.starcases.prime.cli;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

/**
 *
 * Command line interface setup
 *
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
class GraphOpts
{
	/**
	 * Graph type to generate - picocli
	 */
	@Getter
	@Setter
	@Option(names = {"-g", "--graph"}, arity="0..1", description = "Valid vals: ${COMPLETION-CANDIDATES}" )
	private Graph graphType;
}