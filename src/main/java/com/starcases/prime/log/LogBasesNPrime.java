package com.starcases.prime.log;

import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;
import picocli.CommandLine.Command;

/*
* Intent was to log the base type that consisted specifically of some N prime bases
* that sum to each prime#. Not much differentiation between this and LogBase3Triple
* after I did some extensive refactoring - log statement is slighly different but method is same.
*/
@Log
public class LogBasesNPrime extends AbstractLogBase
{
	public LogBasesNPrime(PrimeSourceIntfc ps)
	{
		super(ps, log);
	}
	
	@Override
	@Command
	public void log()
	{
		log.entering("LogBaseNPrime", "log()");
		int idx =0;
		var prIt = ps.getPrimeRefIter();
		
		while (prIt.hasNext())
		{ 				
			try
			{
				var pr = prIt.next();								
				System.out.println(String.format("Prime [%d] %s", pr.getPrime(), pr.getIdxPrimes()));
			}
			catch(Exception e)
			{
				log.severe("Error: " + e);
			}				
		}	
	}
}
