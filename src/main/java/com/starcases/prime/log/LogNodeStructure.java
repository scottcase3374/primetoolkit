package com.starcases.prime.log;

import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;

/**
 * 
 * Logs data about the primes without using the graph structure - instead
 * it just uses my internal api's and displays some of the available info that
 * can be provided.
 *
 */
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
		int idx = 0;
		try
		{
			var prIt = ps.getPrimeRefIter();
			while (prIt.hasNext())
			{ 
				var ref = prIt.next();
				System.out.println(String.format("Prime %d bases %s  <dist[%d], nextPrime[%d]> idx[%d]", 
						ref.getPrime(), 
						ref.getIdxPrimes(), 
						ps.getDistToNextPrime(ref.getPrimeRefIdx()),
						ps.getPrime(ref.getPrimeRefIdx()+1),
						idx++));
			}
		}
		catch(Exception e)
		{
			log.severe("Exception:" + e);
		}
	}
}
