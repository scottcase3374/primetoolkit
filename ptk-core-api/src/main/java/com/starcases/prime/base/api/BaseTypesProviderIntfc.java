package com.starcases.prime.base.api;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

/**
 *
 * @author scott
 *
 */
public interface BaseTypesProviderIntfc extends SvcProviderBaseIntfc
{
	ImmutableMap<String,Enum<?>> baseTypes = Maps.immutable.empty();
	ImmutableList<BaseTypesIntfc> create();
}
