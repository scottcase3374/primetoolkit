package com.starcases.prime.cli;

import java.util.Collections;
import java.util.Set;

import com.starcases.prime.core.api.OutputableIntfc;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

/**
 *
 * Command line interface setup
 *
 */
class OutputOpts
{
	/**
	 * Determines which output options to use
	 */
	@Setter
	@Getter
	@Option(names = {"--output","-o"}, arity = "0..*", description = "Valid vals: ${COMPLETION-CANDIDATES}", converter = OutputableConverter.class)
	private OutputableIntfc [] outputOper;

	/**
	 * Get output oper flags that determine data to output
	 * during processing
	 * @return
	 */
	public Set<OutputableIntfc> getOutputOpers()
	{

		return outputOper == null ? Collections.emptySet() :  Set.of(outputOper);
	}
}