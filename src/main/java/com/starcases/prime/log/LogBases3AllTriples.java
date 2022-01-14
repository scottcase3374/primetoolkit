package com.starcases.prime.log;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

/**
 *
 * Intent was to log the base type that consisted specifically of 3 prime bases
 * that sum to each prime#. Not much differentiation between this and LogBaseNPrime
 * after a refactor.
 *
 */
@Log
public class LogBases3AllTriples  extends AbstractLogBase
{
	public LogBases3AllTriples(@NonNull PrimeSourceIntfc ps)
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
				System.out.println(String.format("Prime [%d] idx[%d] #-bases[%d]\n\t bases %s",
						pr.getPrime(),
						idx++,
						pr.getPrimeBaseIdxs().size(),
						pr.getIdxPrimes()
						));
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
