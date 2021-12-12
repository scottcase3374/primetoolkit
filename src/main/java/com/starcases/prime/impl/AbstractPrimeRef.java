package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.Optional;

import com.starcases.prime.intfc.PrimeRefIntfc;

/**
 * 
 * Abstract class for any common prime ref interface functionality.  
 *
 */
public abstract class AbstractPrimeRef implements PrimeRefIntfc 
{
	
	protected Comparator<BigInteger> bigIntComp = (b1, b2) -> b1.compareTo(b2);
	
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
		return getPrevPrimeRef().map(ppr -> getPrime().subtract(ppr.getPrime()));
	}
}
