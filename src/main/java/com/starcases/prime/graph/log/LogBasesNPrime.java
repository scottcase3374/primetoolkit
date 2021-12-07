package com.starcases.prime.graph.log;

import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

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
		for (var i = 0; i< ps.getMaxIdx(); i++)
		{ 				
			try
			{
				var pr = ps.getPrimeRef(i).get();								
				System.out.println(String.format("Prime [%d] %s", pr.getPrime(), pr.getIdxPrimes()));
			}
			catch(Exception e)
			{
				log.severe("Error: " + e);
			}				
		}	
	}
}
