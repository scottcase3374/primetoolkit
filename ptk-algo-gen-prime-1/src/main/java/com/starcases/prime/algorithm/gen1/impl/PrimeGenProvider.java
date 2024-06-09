package com.starcases.prime.algorithm.gen1.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import com.starcases.prime.core.api.PrimeGenIntfc;
import com.starcases.prime.core.api.PrimeGenProviderIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class PrimeGenProvider implements PrimeGenProviderIntfc
{
	/**
	 * attributes for default provider.
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PRIMEGEN", "DEFAULT");

	/**
	 * constructor - Nothing to do.
	 */
	public PrimeGenProvider()
	{ /* Nothing to do  */ }

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	@Override
	public PrimeGenIntfc create(@NonNull PrimeSourceIntfc primeSrc, @NonNull CollectionTrackerIntfc collTracker)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
