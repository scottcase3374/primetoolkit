package com.starcases.prime.cli;

import java.io.File;
import java.util.Set;

import com.starcases.prime.intfc.OutputableIntfc;

import lombok.Setter;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 *
 * Command line interface setup
 *
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
class OutputOpts
{
	@Parameters(arity="0..1", description = "0 or 1 file name suffix")
	File[] files;

	/**
	 * Determines which output options to use
	 */
	@Setter
	@Option(names = {"--output","-o"}, arity = "0..*", description = "Valid vals: ${COMPLETION-CANDIDATES}", converter = OutputableConverter.class)
	private OutputableIntfc [] outputOper;

	public Set<OutputableIntfc> getOutputOpers()
	{
		return Set.of(outputOper);
	}
}