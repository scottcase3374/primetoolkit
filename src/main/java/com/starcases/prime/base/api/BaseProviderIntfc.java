package com.starcases.prime.base.api;

import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.impl.PrimeBaseGenerator;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

/**
 * Marker interface
 *
 * @author scott
 *
 */
public interface BaseProviderIntfc extends SvcProviderBaseIntfc
{
	PrimeBaseGenerator create(final ImmutableMap<String,Object> settings);
}
