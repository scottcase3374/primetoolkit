package com.starcases.prime.graph.log;

import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

// 
// start 1
//       2
//       3 <-  2 + 1
//       5 <-  3 + 2
//       7 <-  5 + 2
//       11 <- 7+3+1; 5+3+2+1
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
		for (int i = 0; i< ps.getMaxIdx(); i++)
		{ 				
			try
			{
				PrimeRefIntfc pr = ps.getPrimeRef(i);								
				System.out.println(String.format("Prime [%d] %s", pr.getPrime(), pr.getIdxPrimes()));
			}
			catch(Exception e)
			{
				log.severe("Error: " + e);
			}				
		}	
	}
}
