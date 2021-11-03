package com.starcases.prime;

import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeSourceIntfc;

public class PrimeSourceFactory 
{
	private PrimeSourceFactory() {}
	
	public static PrimeSourceIntfc primeSource()
	{
		return new PrimeSource();
	}
}
