package com.starcases.prime.cli;

import java.util.Collections;
import java.util.Set;

import jakarta.validation.constraints.Min;

import com.starcases.prime.base.BaseTypes;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

/**
 *
 * Command Line Interface setups
 *
 */
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
	 * flag indicating whether to redirect output of base info to a file
	 */
	@Getter
	@Setter
	@Option(names = {"--use-base-file"}, description = "Output to a base specific file", required = false)
	private boolean useBaseFile;

	/**
	 * Get bases to process
	 * @return
	 */
	public Set<BaseTypes> getBases()
	{
		return bases == null ? Collections.emptySet() : Set.of(bases);
	}
}