package com.starcases.prime.base.prefix.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.cache.api.PersistedPrefixCacheIntfc;
import com.starcases.prime.base.api.BaseGenFactoryIntfc;

/**
 *
 * @author scott
 *
 */
public class PrefixProvider implements BaseProviderIntfc
{
	/**
	 * Default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PREFIX", "DEFAULT");

	/**
	 * create target service
	 */
	@Override
	public BaseGenFactoryIntfc create(final ImmutableMap<String,Object> settings)
	{
		final PersistedPrefixCacheIntfc cache = (PersistedPrefixCacheIntfc) settings.get("BASES_CACHE");
		return new BasePrefixes(PrefixBaseType.PREFIX, cache);
	}

	/**
	 * get provider attributes
	 */
	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
