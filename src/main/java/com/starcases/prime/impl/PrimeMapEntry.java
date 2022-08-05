package com.starcases.prime.impl;

import com.starcases.prime.intfc.PrimeRefIntfc;

import lombok.Getter;
import lombok.Setter;

/**
 * Provides a mapping from a prime to a prime ref.
 */
class PrimeMapEntry
{
	/**
	 * Current prime
	 */
	@Getter
	@Setter
	private long prime;

	/**
	 * related prime ref.
	 */
	@Getter
	@Setter
	private PrimeRefIntfc primeRef;

	/**
	 * Constructor
	 * @param prime
	 * @param primeRef
	 */
	public PrimeMapEntry(final long prime, final PrimeRefIntfc primeRef)
	{
		this.prime = prime;
		this.primeRef = primeRef;
	}
}
