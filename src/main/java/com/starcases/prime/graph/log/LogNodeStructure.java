package com.starcases.prime.graph.log;

import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;

// 
// start 1
//       2
//       3 <-  2 + 1
//       5 <-  3 + 2
//       7 <-  5 + 2
//       11 <- 7+3+1; 5+3+2+1
@Log
public class LogNodeStructure extends AbstractLogBase
{
	public LogNodeStructure(PrimeSourceIntfc ps)
	{
		super(ps, log);
	}
	
	@Override
	public void log()
	{
		try
		{
			for (int i = 0; i < ps.getMaxIdx(); i++)
			{ 
				PrimeRefIntfc ref = ps.getPrimeRef(i);
				System.out.println(String.format("Prime %d bases %s  <dist[%d], nextPrime[%d]>", 
						ref.getPrime(), 
						ref.getIdxPrimes(), 
						ps.getDistToNextPrime(ref.getPrimeRefIdx()),
						ps.getPrime(ref.getPrimeRefIdx()+1)));
			}
		}
		catch(Exception e)
		{
			log.severe("Exception:" + e);
		}
	}
}
