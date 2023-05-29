package com.starcases.prime.base.prefix.impl;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.impl.PrimeBaseGenerator;
import com.starcases.prime.core.api.PrimeRefIntfc;

import lombok.NonNull;

/**
 * Produces prefix list for each prime
 *
 */
class BasePrefixes extends PrimeBaseGenerator
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
	public void genBasesForPrimeRef(final PrimeRefIntfc curPrime)
	{
		final var origBases = curPrime.getPrimeBaseData().getPrimeBases().get(0);
		curPrime.getPrimeBaseData().addPrimeBases(Lists.mutable.of(origBases), BaseTypes.PREFIX);
	}
}
