package com.starcases.prime.metrics.api;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

public interface PredefinedMetricsProviderIntfc extends SvcProviderBaseIntfc
{
	String getProviderName();
	void create(ImmutableMap<String, Object> attributes);
}
