package com.starcases.prime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.base_api.BaseTypes;
import com.starcases.prime.cli.DefaultInit;

import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

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
	private static Map<String, Path> outputs = new ConcurrentHashMap<>();


	/**
	 * Associate a path instance to a key where the path is
	 * used to sink output data for that key.
	 *
	 * @param key
	 * @param outputPath
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	public static void setOutput(final String key, final Path outputPath)
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("set output [key=%s, path=%s]", key, outputPath.toAbsolutePath().toString()));
		}
		outputs.computeIfAbsent(key, func -> outputPath.toAbsolutePath());
	}

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

	/**
	 * Method to send out to a printstream which may by
	 * default be stdout or renamed to a specific file.
	 *
	 * @param format
	 * @param params
	 * @throws IOException
	 */
	public static void output(final BaseTypes baseType, final String format, final Object...params)
	{
		output(baseType.toString(), format, params);
	}

	/**
	 * Method to send out to a printstream which may by
	 * default be stdout or renamed to a specific file.
	 *
	 * @param format
	 * @param params
	 * @throws IOException
	 */
	@SuppressWarnings("PMD.SystemPrintln")
	public static void output(final String baseType, final String format, final Object...params)
	{
		final var path = outputs.get(baseType);
		if (path != null)
		{
			try
			{
				Files.writeString(path, String.format(format, params), StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			}
			catch (final IOException e)
			{
				if (LOG.isLoggable(Level.SEVERE))
				{
					LOG.severe("Unable to write to file:" + path.toString());
				}
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.info(String.format(format, params));
				}
			}
		}
		else
		{
			System.out.printf(String.format("%s%n", format), params);
		}
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
		output("stdout", format, params);
	}

	/**
	 * method for debug out; same as regular but having
	 * a different method provides an easier way to search
	 * for items which don't need to remain in the code base.
	 *
	 * @param format
	 * @param params
	 */
	public static void dbgOutput(final BaseTypes baseType, final String format, final Object...params)
	{
		output(baseType, format, params);
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
		output(BaseTypes.DEFAULT,format, params);
	}
}

