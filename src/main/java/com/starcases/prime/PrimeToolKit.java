package com.starcases.prime;

import java.util.logging.Level;

import com.starcases.prime.cli.DefaultInit;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

/**
 *
 * CLI Driver
 *
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
@Getter
@Setter
@Command(name = "ptk", subcommands = { DefaultInit.class, CommandLine.HelpCommand.class }  , description="Prime Tool Kit")
public final class PrimeToolKit
{
	/**
	 *  Picocli command spec object for handling command line args.
	 */
	private @Spec CommandSpec spec;

	/**
	 * entry point
	 *
	 * see docs README.md for more info.
	 *
	 * @param args
	 */
	public static void main(@NonNull final String [] args)
	{
		final var ptk = new PrimeToolKit();
		final var commandLine = new CommandLine(ptk);
		commandLine.registerConverter(Level.class, Level::parse);
		final var exitCode = commandLine.execute(args);

		System.exit(exitCode);
	}
}

