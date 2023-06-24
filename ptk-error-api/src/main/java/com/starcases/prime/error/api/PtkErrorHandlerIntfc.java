package com.starcases.prime.error.api;

import java.util.function.Supplier;
import java.util.logging.Level;

import lombok.NonNull;

public interface PtkErrorHandlerIntfc
{
	PtkErrorHandlerIntfc handleError(@NonNull final Supplier<String> msgSupplier,
									 @NonNull final Level logLevel,
									 @NonNull final Exception exception,
									 final boolean rethrow,
									 final boolean defaultOutputAlso

									 );

	PtkErrorHandlerIntfc handleError(@NonNull final Supplier<String> msgSupplier,
									 @NonNull final Level logLevel,
									 final boolean defaultOutputAlso
									 );
}
