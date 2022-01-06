package com.starcases.prime;

import javax.validation.constraints.Min;

import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

import com.starcases.prime.intfc.BaseTypes;

public class PrimeSourceFactory 
{
	private PrimeSourceFactory() 
	{}

	public static PrimeSourceIntfc primeSource(@Min(1) int maxCount, @Min(1) int confidenceLevel, @NonNull BaseTypes activeBaseId)
	{
		var ps = new PrimeSource(maxCount, confidenceLevel);
		ps.setActiveBaseId(activeBaseId);
		return ps;
	}
	
	public static PrimeSourceIntfc primeSource(@Min(1) int maxCount, @Min(1) int confidenceLevel)
	{
		return new PrimeSource(maxCount, confidenceLevel);
	}
}
