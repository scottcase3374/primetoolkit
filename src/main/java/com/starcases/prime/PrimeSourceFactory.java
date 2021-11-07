package com.starcases.prime;

import com.starcases.prime.graph.impl.PrimeGrapher;
import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeSourceIntfc;

public class PrimeSourceFactory 
{
	private PrimeSourceFactory() {}
	
	public static PrimeSourceIntfc primeSource(int maxCount)
	{
		return new PrimeSource(maxCount);
	}
	
	public static PrimeGrapher primeGrapher(int maxCount)
	{
		
		return new PrimeGrapher(primeSource(maxCount), maxCount);
	}
}
