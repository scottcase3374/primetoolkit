package com.starcases.prime.cli;

import java.util.Set;

import javax.validation.constraints.Min;

import com.starcases.prime.base.BaseTypes;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

/**
 *
 * Command Line Interface setups
 *
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
class BaseOpts
{
	/**
	 * Base info selected - picocli
	 */
	@Setter
	@Option(names = {"-b", "--base"}, arity = "0..*", description = "Valid vals: ${COMPLETION-CANDIDATES}")
	private BaseTypes[] bases;

	/**
	 * max reduce value - picocli
	 */
	@Getter
	@Setter
	@Min(2)
	@Option(names = {"--max-reduce"}, description = "Max base value [1-max) to use for NPrime.", defaultValue="4", required = false)
	private int maxReduce;

	/**
	 * Get bases to process
	 * @return
	 */
	public Set<BaseTypes> getBases()
	{
		return Set.of(bases);
	}
}