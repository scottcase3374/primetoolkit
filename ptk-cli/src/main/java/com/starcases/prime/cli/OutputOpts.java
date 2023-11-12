package com.starcases.prime.cli;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Sets;

import com.starcases.prime.kern.api.OutputableIntfc;

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
	@Option(names = {"--output","-o"}, arity = "0..*", description = "Valid vals: PREFIX,TRIPLE,NPRIME,PRIME_TREE", converter = OutputableConverter.class)
	private OutputableIntfc [] outputOper;

	/**
	 * Get output oper flags that determine data to output
	 * during processing
	 * @return
	 */
	public ImmutableCollection<OutputableIntfc> getOutputOpers()
	{
		return outputOper == null ? Sets.immutable.empty() :  Sets.immutable.of(outputOper);
	}
}