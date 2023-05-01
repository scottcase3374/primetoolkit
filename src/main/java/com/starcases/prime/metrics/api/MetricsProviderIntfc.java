package com.starcases.prime.metrics.api;

import org.eclipse.collections.api.map.ImmutableMap;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

/**
 * Interface for use with service loader to
 * load Export based services.
 * @author scott
 *
 */
public interface MetricsProviderIntfc extends SvcProviderBaseIntfc
{
	MetricIntfc create(final ImmutableMap<String, Object> attributes);
}
