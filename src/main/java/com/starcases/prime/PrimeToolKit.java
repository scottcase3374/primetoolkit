package com.starcases.prime;

import java.util.logging.Level;
import com.starcases.prime.cli.Init;
import lombok.extern.java.Log;
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
@Log
@Command(name = "ptk", subcommands = { Init.class, CommandLine.HelpCommand.class }  , description="Prime Tool Kit")
public class PrimeToolKit
{
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

