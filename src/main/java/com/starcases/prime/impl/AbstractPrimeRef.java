package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.Optional;

import com.starcases.prime.intfc.PrimeRefIntfc;

/**
 *
 * Abstract class for common prime ref interface functionality.
 *
 */
abstract class AbstractPrimeRef implements PrimeRefIntfc
{
	public AbstractPrimeRef()
	{}

	public PrimeRefIntfc getPrimeRef()
	{
		return this;
	}

	/**
	 * absolute value of difference with next prime
	 * if the next prime is known.
	 *
	 * empty optional if next prime is unknown/not calculated
	 */
	@Override
	public Optional<BigInteger> getDistToNextPrime()
	{
		return  getNextPrimeRef().map(npr -> npr.getPrime().subtract(getPrime()));
	}

	/**
	 * absolute value of difference with prev prime
	 * if the prev prime is known/exists.
	 *
	 * empty optional if prev prime is unknown/doesn't exist
	 */
	@Override
	public Optional<BigInteger> getDistToPrevPrime()
	{
		return getPrevPrimeRef().map(ppr -> ppr.getPrime().subtract(getPrime()));
	}
}
