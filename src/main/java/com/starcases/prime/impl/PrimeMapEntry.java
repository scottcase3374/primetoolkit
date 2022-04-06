package com.starcases.prime.impl;

import java.math.BigInteger;

import com.starcases.prime.intfc.PrimeRefIntfc;

import lombok.Getter;
import lombok.Setter;

class PrimeMapEntry
{
	@Getter
	@Setter
	private BigInteger prime;

	@Getter
	@Setter
	private PrimeRefIntfc primeRef;

	public PrimeMapEntry(BigInteger prime, PrimeRefIntfc primeRef)
	{
		this.prime = prime;
		this.primeRef = primeRef;
	}
}
