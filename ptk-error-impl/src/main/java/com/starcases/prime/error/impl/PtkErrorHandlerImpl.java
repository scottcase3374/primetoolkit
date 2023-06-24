package com.starcases.prime.error.impl;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.error.api.PtkErrorHandlerIntfc;

import lombok.NonNull;

public class PtkErrorHandlerImpl implements PtkErrorHandlerIntfc
{
	private static final Logger LOG = Logger.getLogger(PtkErrorHandlerImpl.class.getName());

	public PtkErrorHandlerIntfc handleError(@NonNull final Supplier<String> msgSupplier,
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
				throw new RuntimeException(exception);
			}
		}
		return this;
	}

	public PtkErrorHandlerIntfc handleError(@NonNull final Supplier<String> msgSupplier,
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
