package com.starcases.prime.graph.log;

import com.starcases.prime.graph.impl.PrimeGrapher;
import com.starcases.prime.intfc.LogGraphIntfc;
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
public class LogBases3Triple extends PrimeGrapher implements LogGraphIntfc
{
	public LogBases3Triple(PrimeSourceIntfc ps)
	{
		super(ps, log);
	}
	
	@Override
	@Command
	public void log()
	{
		// Get desired data
		ps.setActiveBaseId(1);
		for (int i = 0; i < ps.getMaxIdx(); i++)
		{ 				
			PrimeRefIntfc pr = ps.getPrimeRef(i);
			try 
			{
				System.out.println(String.format("Prime [%d] idx[%d] bases %s base-indexes %s",
						pr.getPrime(), 
						i,
						pr.getIdxPrimes(),
						pr.getIndexes()));
			}
			catch(Exception e)
			{
				System.out.println(String.format("Can't show bases for: %d", pr.getPrime()));
			}		
		}	
	}
}
