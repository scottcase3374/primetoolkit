package com.starcases.prime;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.cli.Init;
import lombok.NonNull;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

/**
 *
 * CLI Driver
 *
 */
@Command(name = "ptk", subcommands = { Init.class, CommandLine.HelpCommand.class }  , description="Prime Tool Kit")
public class PrimeToolKit
{
	private static final Logger log = Logger.getLogger(PrimeToolKit.class.getName());

	@Spec CommandSpec spec;

	public static void main(@NonNull String [] args)
	{
		final var ptk = new PrimeToolKit();
		final var cl = new CommandLine(ptk);
		cl.registerConverter(java.util.logging.Level.class, Level::parse);
		final var exitCode = cl.execute(args);

		System.exit(exitCode);
		log.info("exited");
	}
}

