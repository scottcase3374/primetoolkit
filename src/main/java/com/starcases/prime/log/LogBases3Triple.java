package com.starcases.prime.log;

import java.math.BigInteger;
import java.util.Optional;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

/**
 * 
 * Intent was to log the base type that consisted specifically of 3 prime bases
 * that sum to each prime#. Not much differentiation between this and LogBaseNPrime
 * after I did some extensive refactoring.
 *
 */
@Log
public class LogBases3Triple  extends AbstractLogBase
{
	public LogBases3Triple(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}
	
	@Override
	@Command
	public void log()
	{
		// Get desired data
		ps.setActiveBaseId(BaseTypes.THREETRIPLE);
	
		int idx = 0;
		var prIt = ps.getPrimeRefIter();
		while (prIt.hasNext())
		{ 				
			var pr = prIt.next();
			try 
			{
				BigInteger sum = pr.getPrimeBaseIdxs()
						.stream()
						.boxed()
						.map(ps::getPrimeRef)
						.filter(Optional::isPresent)
						.map(p -> p.get().getPrime())
						.reduce(BigInteger.ZERO, (a,b) -> a.add(b));
				
				System.out.println(String.format("Prime [%d] idx[%d] bases %s => sum: %d %s",
						pr.getPrime(), 
						idx++,
						pr.getIdxPrimes(),
						sum, pr.getPrime().equals(sum) ? "":  " ## SUM MISMATCH")) ;
			}
			catch(Exception e)
			{
				log.severe(String.format("Can't show bases for: %d exception:", pr.getPrime()));
				log.throwing(this.getClass().getName(), "log", e);
				e.printStackTrace();
			}		
		}	
	}
}
