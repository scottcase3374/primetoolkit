package com.starcases.prime.metrics.api;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.core.api.OutputableIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface MetricProviderIntfc extends SvcProviderBaseIntfc
{
	MetricProviderIntfc create(final ImmutableMap<String,Object> settings);
	MetricIntfc timer(@NonNull final OutputableIntfc outputable, @NonNull final String...tags);
	MetricIntfc  longTimer(@NonNull final OutputableIntfc outputable, final String...tags);
}
