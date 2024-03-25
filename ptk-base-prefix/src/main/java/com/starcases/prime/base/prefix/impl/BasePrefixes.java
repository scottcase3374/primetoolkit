package com.starcases.prime.base.prefix.impl;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.base.api.BaseGenFactoryIntfc;
import com.starcases.prime.base.impl.AbsPrimeBaseGen;
import com.starcases.prime.cache.api.PersistedPrefixCacheIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;

import lombok.NonNull;

/**
 * Produces prefix list for each prime
 *
 */
class BasePrefixes extends AbsPrimeBaseGen
{
	/**
	 * constructor for creation of Base prefixes.
	 * @param primeSrc
	 */
	public BasePrefixes(@NonNull final BaseTypesIntfc baseType, PersistedPrefixCacheIntfc cache)
	{
		super(baseType, cache);
	}

	/**
	 * Generate the base info for the curPrime.
	 */
	@Override
	public void genBasesForPrimeRef(@NonNull final PrimeRefIntfc curPrime)
	{
		final var origBases = findPrefixesLowFirst(curPrime);
		curPrime.getPrimeBaseData().addPrimeBases(curPrime.getPrimeRefIdx(), Lists.mutable.of(origBases), PrefixBaseType.PREFIX);
	}

	/**
	 * fluent style method for setting flag for whether base construction can use multiple CPU cores.
	 * @param preferParallel Ignored for BasePrefixes - must always be false
	 *
	 * @return
	 */
	@Override
	public BaseGenFactoryIntfc doPreferParallel(final boolean preferParallel)
	{
		this.preferParallel = false;
		return this;
	}
}
