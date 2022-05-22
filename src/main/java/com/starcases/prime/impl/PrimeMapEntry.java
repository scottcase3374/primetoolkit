package com.starcases.prime.impl;

import java.math.BigInteger;

import com.starcases.prime.intfc.PrimeRefIntfc;

import lombok.Getter;
import lombok.Setter;

/**
 * Provides a mapping from a prime to a prime ref.
 */
class PrimeMapEntry
{
	@Getter
	@Setter
	private BigInteger prime;

	@Getter
	@Setter
	private PrimeRefIntfc primeRef;

	/**
	 * Constructor
	 * @param prime
	 * @param primeRef
	 */
	public PrimeMapEntry(final BigInteger prime, final PrimeRefIntfc primeRef)
	{
		this.prime = prime;
		this.primeRef = primeRef;
	}
}
