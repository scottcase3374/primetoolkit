package com.starcases.prime.graph.export.api;

import java.io.PrintWriter;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

/**
 * Interface for use with service loader to
 * load Export based services.
 * @author scott
 *
 */
public interface ExportsProviderIntfc extends SvcProviderBaseIntfc
{
	ExportIntfc create(	@NonNull final PrimeSourceIntfc primeSrc,
						@NonNull final PrintWriter outputWriter,
						final ImmutableMap<String, Object> attributes);
}
