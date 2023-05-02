package com.starcases.prime.core.api;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

public interface ProgressProviderIntfc extends SvcProviderBaseIntfc
{
	ProgressIntfc create(final ImmutableMap<String,Object> settings);
}
