package com.starcases.prime.base.impl;

import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.api.PrimeBaseGeneratorIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 *
 * Abstract class for common Prime base functionality.
 *
 */
public abstract class PrimeBaseGenerator implements PrimeBaseGeneratorIntfc
{
	@Getter
	private final BaseTypesIntfc baseType;
	/**
	 * Access to lookup of prime/primerefs and the init of base information.
	 */
	@Getter(AccessLevel.PROTECTED)
	protected PrimeSourceIntfc primeSrc;

	/**
	 * Indicator for whether a sum is over/under/equal desired value or must-undo change
	 */
	protected enum State { OVER, UNDER, EQUAL, REVERT }

	/**
	 * Flag indicating whether base construction can use multiple CPU cores
	 */
	@Getter(AccessLevel.PROTECTED)
	protected boolean preferParallel;


	/**
	 * Constructor for secondary bases.
	 * @param primeSrc
	 */
	protected PrimeBaseGenerator(@NonNull final BaseTypesIntfc baseType)
	{
		this.baseType = baseType;
	}

	/**
	 * fluent style method for setting flag for whether base construction can use multiple CPU cores.
	 * @param preferParallel
	 * @return
	 */
	public PrimeBaseGeneratorIntfc doPreferParallel(final boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	@Override
	public PrimeBaseGeneratorIntfc assignPrimeSrc(final PrimeSourceIntfc primeSrc)
	{
		this.primeSrc = primeSrc;
		return this;
	}
}
