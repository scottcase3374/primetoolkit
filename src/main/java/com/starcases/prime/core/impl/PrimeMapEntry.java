package com.starcases.prime.core.impl;

import com.starcases.prime.core.api.PrimeRefIntfc;

import lombok.Getter;
import lombok.Setter;

/**
 * Provides a mapping from a prime to a prime ref.
 */
public class PrimeMapEntry
{
	/**
	 * Current prime
	 */
	@Getter
	@Setter
	protected long prime = -1;

	/**
	 * related prime ref.
	 */
	@Getter
	@Setter
	protected PrimeRefIntfc primeRef;

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

	/**
	 * No arg constructor to appease code-analysis/metrics
	 */
	public PrimeMapEntry()
	{
	}
}
