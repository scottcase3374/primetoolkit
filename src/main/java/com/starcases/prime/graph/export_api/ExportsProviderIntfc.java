package com.starcases.prime.graph.export_api;

import java.io.PrintWriter;

import org.eclipse.collections.api.collection.ImmutableCollection;

import com.starcases.prime.core_api.PrimeSourceIntfc;

import lombok.NonNull;

/**
 * Interface for use with service loader to
 * load Export based services.
 * @author scott
 *
 */
public interface ExportsProviderIntfc
{
	ExportIntfc create(final ImmutableCollection<String> attributes, @NonNull final PrimeSourceIntfc primeSrc, @NonNull final PrintWriter outputWriter);
}
