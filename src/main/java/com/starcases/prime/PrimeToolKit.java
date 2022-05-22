package com.starcases.prime;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.cli.Init;

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
@Command(name = "ptk", subcommands = { Init.class, CommandLine.HelpCommand.class }  , description="Prime Tool Kit")
public final class PrimeToolKit
{
	/**
	 *  local logger
	 */
	private static final Logger LOG = Logger.getLogger(PrimeToolKit.class.getName());

	/**
	 *  Picocli command spec object for handling command line args.
	 */
	private @Spec CommandSpec spec;

	/**
	 * printstream used by the out() and dbgOut() methods.
	 */
	@Getter
	@Setter
	private static PrintStream out = System.out;

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
		LOG.info("exited");
	}

	/**
	 * Method to send out to a printstream which may by
	 * default be stdout or renamed to a specific file.
	 *
	 * @param format
	 * @param params
	 */
	public static void output(final String format, final Object...params)
	{
		out.format(format, params);
	}

	/**
	 * method for debug out; same as regular but having
	 * a different method provides an easier way to search
	 * for items which don't need to remain in the code base.
	 *
	 * @param format
	 * @param params
	 */
	public static void dbgOutput(final String format, final Object...params)
	{
		out.format(format, params);
	}
}

