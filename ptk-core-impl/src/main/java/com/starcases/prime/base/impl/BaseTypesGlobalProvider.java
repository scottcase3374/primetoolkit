package com.starcases.prime.base.impl;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.service.impl.SvcLoader;

public class BaseTypesGlobalProvider implements BaseTypesProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("GLOBAL_BASE_TYPES");
	private static final ImmutableList<String> BASE_PROVIDERS_ATTRIBUTES = Lists.immutable.of("BASE_TYPES");

	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	@Override
	public ImmutableList<BaseTypesIntfc> create()
	{
		final SvcLoader<BaseTypesProviderIntfc, Class<BaseTypesProviderIntfc>> baseTypeProviders = new SvcLoader< >(BaseTypesProviderIntfc.class);
		return baseTypeProviders
				.providers(BASE_PROVIDERS_ATTRIBUTES)
				//.tap(p -> System.out.println("BaseTypesGlobalProvider provider attrs:" + p.getProviderAttributes().makeString()))
				.flatCollect(BaseTypesProviderIntfc::create);
				//.tap(p -> System.out.println("BaseTypesGlobalProvider provider:" + p.name()))
				//.toImmutableList();
	}
}
