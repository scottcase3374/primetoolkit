package com.starcases.prime.base.prefix;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public class BasePrefixes extends AbstractPrimeBaseGenerator
{
	public BasePrefixes(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	@Override
	public void genBases()
	{
		log.info("BasePrefixes genBases()");

		final var prStream = ps.getPrimeRefStream(preferParallel);
		prStream.forEach(pr ->
				{
					try
					{
						final var origIdxs = pr.getPrimeBaseData().getPrimeBaseIdxs().get(0);

						// We don't include the Pn-1 idx in prefix list
						pr.getPrimeBaseData().addPrimeBase(origIdxs.subList(0, origIdxs.size()-1), BaseTypes.PREFIX);
					}
					catch(Exception e)
					{
						log.severe(String.format("Can't show bases for: %d exception:", pr.getPrime()));
						log.throwing(this.getClass().getName(), "log", e);
						e.printStackTrace();
					}
				});
	}
}
