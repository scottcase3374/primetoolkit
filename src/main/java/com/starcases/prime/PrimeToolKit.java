package com.starcases.prime;

import com.starcases.prime.impl.PrimeSource;

public class PrimeToolKit 
{
	public static void main(String [] args)
	{
		int maxCount = 1500;
		if (args != null && args[0] != null)
		{
			try
			{
				maxCount = Integer.parseInt(args[0]);
			}
			catch(Exception e)
			{}
		}
		PrimeSource ps = new PrimeSource(maxCount);
		
		//PrimeGrapher primeGrapher = new PrimeGrapher();		
		//boolean debug = true;
		//primeGrapher.populateData(targetRows, debug);
		//primeGrapher.logGraphStructure();
		//primeGrapher.setNodeLocations();
		//primeGrapher.viewDefault();
	}
}
