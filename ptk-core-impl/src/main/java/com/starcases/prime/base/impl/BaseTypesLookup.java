package com.starcases.prime.base.impl;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;

import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.NonNull;

public class BaseTypesLookup
{
	public static MutableMap<String, BaseTypesIntfc> get(@NonNull MutableMap<String, BaseTypesIntfc> baseTypesColl, @NonNull final ImmutableList<String> attributes)
	{
		final SvcLoader<BaseTypesProviderIntfc, Class<BaseTypesProviderIntfc>> baseTypeProviders = new SvcLoader< >(BaseTypesProviderIntfc.class);
		return baseTypeProviders
				.providers(attributes)
				.flatCollect(BaseTypesProviderIntfc::create)
				.tap(p -> System.out.println("BaseTypesLookup provider:" + p.name()))
				.toMap(BaseTypesIntfc::name, (f) -> f, baseTypesColl);
	}
}
