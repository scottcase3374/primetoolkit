package com.starcases.prime.base.primetree.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.api.LogPrimeDataProviderIntfc;
import com.starcases.prime.core.api.LogPrimeDataIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class LogPrimeTreeDataProvider implements LogPrimeDataProviderIntfc
{
	/**
	 * Default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRIME_TREE", "DEFAULT");

	/**
	 * Get provider attributes
	 */
	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	/**
	 * Create target service
	 */
	@Override
	public LogPrimeDataIntfc create(@NonNull final PrimeSourceIntfc primeSrc, final ImmutableMap<String,Object> settings)
	{
		return new LogPrimeTree(primeSrc);
	}
}
