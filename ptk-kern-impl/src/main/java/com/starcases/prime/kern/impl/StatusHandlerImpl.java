package com.starcases.prime.kern.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.kern.api.PtkException;
import com.starcases.prime.kern.api.StatusHandlerIntfc;

import lombok.NonNull;

public class StatusHandlerImpl implements StatusHandlerIntfc
{
	/**
	 *  local logger
	 */
	private static final Logger LOG = Logger.getLogger(StatusHandlerImpl.class.getName());

	private static MutableMap<String, Path> outputs = new ConcurrentHashMap<>();

	/**
	 * Associate a path instance to a key where the path is
	 * used to sink output data for that key.
	 *
	 * @param key
	 * @param outputPath
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	public void setOutput(final String key, final Path outputPath)
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("set output [key=%s, path=%s]", key, outputPath.toAbsolutePath().toString()));
		}

		outputs.putIfAbsent(key, outputPath.toAbsolutePath());
	}

	/**
	 * Method to send out to a printstream which may by
	 * default be stdout or renamed to a specific file.
	 *
	 * @param format
	 * @param params
	 * @throws IOException
	 */
	public void output(final BaseTypesIntfc baseType, final String format, final Object...params)
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
	private void output(final String baseType, final String format, final Object...params)
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
	public void output(final String format, final Object...params)
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
	public void dbgOutput(final BaseTypesIntfc baseType, final String format, final Object...params)
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
	public void dbgOutput(final String format, final Object...params)
	{
		output("DEFAULT",format, params);
	}

	/**
	 * method for error out
	 *
	 * @param format
	 * @param params
	 */
	public void errorOutput(final BaseTypesIntfc baseType, final String format, final Object...params)
	{
		output(baseType, format, params);
	}

	/**
	 * method for error out
	 *
	 * @param format
	 * @param params
	 */
	public void errorOutput(final String format, final Object...params)
	{
		output("DEFAULT",format, params);
	}


	public StatusHandlerIntfc handleError(@NonNull final Supplier<String> msgSupplier,
			 @NonNull final Level logLevel,
			 @NonNull final Exception exception,
			 final boolean rethrow,
			 final boolean defaultOutputAlso /* to be handled at some point - need refactor */
			 )
	{
		if (LOG.isLoggable(logLevel))
		{
			LOG.log(logLevel, exception, msgSupplier);
		}

		if (rethrow)
		{
			if (exception instanceof RuntimeException rte)
			{
				throw rte;
			}
			else
			{
				throw new PtkException(exception);
			}
		}
		return this;
	}

	public StatusHandlerIntfc handleError(@NonNull final Supplier<String> msgSupplier,
			 @NonNull final Level logLevel,
			 final boolean defaultOutputAlso /* to be handled at some point - need refactor */
			 )
	{
		if (LOG.isLoggable(logLevel))
		{
			LOG.log(logLevel, msgSupplier);
		}
		return this;
	}
}
