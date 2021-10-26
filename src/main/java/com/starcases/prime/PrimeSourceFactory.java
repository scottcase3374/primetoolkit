package com.starcases.prime;

import com.starcases.prime.impl.PrimeComparators;
import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeSourceIntfc;

public class PrimeSourceFactory 
{
	static PrimeComparators  pc = new PrimeComparators();
	
	public static <E extends Number & Comparable<E>> PrimeSourceIntfc<E> primeSource()
	{
		return new PrimeSource<E>(pc);
	}
}
