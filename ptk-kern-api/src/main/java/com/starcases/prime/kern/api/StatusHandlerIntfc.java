package com.starcases.prime.kern.api;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.logging.Level;

import lombok.NonNull;

public interface StatusHandlerIntfc
{
	StatusHandlerIntfc handleError(@NonNull final Supplier<String> msgSupplier,
									 @NonNull final Level logLevel,
									 @NonNull final Exception exception,
									 final boolean rethrow,
									 final boolean defaultOutputAlso

									 );

	StatusHandlerIntfc handleError(@NonNull final Supplier<String> msgSupplier,
									 @NonNull final Level logLevel,
									 final boolean defaultOutputAlso
									 );

	void setOutput(final String key, final Path outputPath);
	void output(final BaseTypesIntfc baseType, final String format, final Object...params);
	void output(final String format, final Object...params);
	void dbgOutput(final BaseTypesIntfc baseType, final String format, final Object...params);
	void dbgOutput(final String format, final Object...params);
	void errorOutput(final BaseTypesIntfc baseType, final String format, final Object...params);
	void errorOutput(final String format, final Object...params);
}
