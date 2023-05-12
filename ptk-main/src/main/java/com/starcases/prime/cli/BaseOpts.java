package com.starcases.prime.cli;

import java.util.Collections;
import java.util.Set;

import com.starcases.prime.base.api.BaseTypesIntfc;

import jakarta.validation.constraints.Min;
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
	private BaseTypesIntfc[] bases;

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
	 * flag for enabling listening for remote commands based on a SQL like grammar.
	 */
	@Getter
	@Setter
	@Option(names = {"--enable-cmd-listener"}, description = "Listen for remote commands", required = false)
	private boolean enableCmmandListener;

	/**
	 * flag for setting port of command listener (which processes SQL-like commands, etc).
	 */
	@Getter
	@Setter
	@Option(names = {"--cmd-listener-port"}, description = "Port for the command (SQL) listener", defaultValue = "8690", required = false )
	private int cmdListenerPort;



	/**
	 * Get bases to process
	 * @return
	 */
	public Set<BaseTypesIntfc> getBases()
	{
		return bases == null ? Collections.emptySet() : Set.of(bases);
	}
}