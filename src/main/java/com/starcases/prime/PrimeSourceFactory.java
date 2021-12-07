package com.starcases.prime;

import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.intfc.BaseTypes;

public class PrimeSourceFactory 
{
	private PrimeSourceFactory() 
	{}

	public static PrimeSourceIntfc primeSource(int maxCount, int confidenceLevel, BaseTypes activeBaseId)
	{
		var ps = new PrimeSource(maxCount, confidenceLevel);
		ps.setActiveBaseId(activeBaseId);
		return ps;
	}
	
	public static PrimeSourceIntfc primeSource(int maxCount, int confidenceLevel)
	{
		return new PrimeSource(maxCount, confidenceLevel);
	}
}
