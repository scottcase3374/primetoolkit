package com.starcases.prime.graph.log;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

@Log
public class LogBases3Triple  extends AbstractLogBase
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
		ps.setActiveBaseId(BaseTypes.THREETRIPLE);
	
		for (var i = 0; i < ps.getMaxIdx(); i++)
		{ 				
			var pr = ps.getPrimeRef(i).get();
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
				log.severe(String.format("Can't show bases for: %d exception: %s", pr.getPrime(), e.toString()));
			}		
		}	
	}
}
