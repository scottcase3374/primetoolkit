package com.starcases.prime.impl;

import java.math.BigInteger;

import com.starcases.prime.intfc.PrimeRefIntfc;

import lombok.Getter;
import lombok.Setter;

class PrimeMapEntry
{
	//private WeakReference<BigInteger> prime;
	private BigInteger prime;

	@Getter
	@Setter
	private PrimeRefIntfc primeRef;

	public PrimeMapEntry(BigInteger prime, PrimeRefIntfc primeRef)
	{
		this.prime = prime; //new WeakReference<BigInteger>(prime);
		this.primeRef = primeRef;
	}

	public BigInteger getPrime()
	{
		return prime; //prime.get();
	}

	public void setPrime(BigInteger prime)
	{
		this.prime = prime; // new WeakReference<BigInteger>(prime);
	}
}
