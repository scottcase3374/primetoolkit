package com.starcases.prime;

import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeSourceIntfc;

public class PrimeSourceFactory 
{
	private PrimeSourceFactory() 
	{}

	public static PrimeSourceIntfc primeSource(int maxCount, int confidenceLevel, int activeBaseId)
	{
		PrimeSource ps = new PrimeSource(maxCount, confidenceLevel);
		ps.setActiveBaseId(activeBaseId);
		return ps;
	}
	
	public static PrimeSourceIntfc primeSource(int maxCount, int confidenceLevel)
	{
		return new PrimeSource(maxCount, confidenceLevel);
	}
	
	/*public static PrimeGrapher primeGrapher(int maxCount, int confidenceLevel)
	{
		
		return new PrimeGrapher(primeSource(maxCount, confidenceLevel));
	}

	public static PrimeGrapher primeGrapher(int maxCount, int confidenceLevel, int activeBaseId)
	{
		
		return new PrimeGrapher(primeSource(maxCount, confidenceLevel, activeBaseId));
	}*/
}
