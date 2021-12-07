package com.starcases.prime.graph.log;

import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;

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
			for (var i = 0; i < ps.getMaxIdx(); i++)
			{ 
				var ref = ps.getPrimeRef(i).get();
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
