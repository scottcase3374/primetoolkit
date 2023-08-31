package com.starcases.prime.cli;

import com.starcases.prime.kern.api.BaseTypesIntfc;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

/**
 *
 * Command line interface setup
 *
 */
class GraphOpts
{
	/**
	 * Graph type to generate - picocli
	 */
	@Setter
	@Getter
	@Option(names = {"-g", "--graph"}, arity="0..1", description = "Valid vals: ${COMPLETION-CANDIDATES}", converter = BaseTypesIntfcConverter.class )
	private BaseTypesIntfc graphType;
}