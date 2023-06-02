package com.starcases.prime.base.prefix.impl;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.base.api.BaseGenFactoryIntfc;
import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.impl.AbsPrimeBaseGen;
import com.starcases.prime.core.api.PrimeRefIntfc;

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
	public BasePrefixes(@NonNull final BaseTypesIntfc baseType)
	{
		super(baseType);
	}

	/**
	 * This is a pretty quick and dirty implementation.  Maybe find some improvements later.
	 */
	@Override
	public void genBasesForPrimeRef(@NonNull final PrimeRefIntfc curPrime)
	{
		final var origBases = curPrime.getPrimeBaseData().getPrimeBases().get(0);
		curPrime.getPrimeBaseData().addPrimeBases(Lists.mutable.of(origBases), PrefixBaseType.PREFIX);
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
