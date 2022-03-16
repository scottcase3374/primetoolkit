package com.starcases.prime.base.prefix;

import java.util.TreeSet;
import java.util.logging.Logger;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

public class BasePrefixes extends AbstractPrimeBaseGenerator
{
	private static final Logger log = Logger.getLogger(BasePrefixes.class.getName());

	public BasePrefixes(@NonNull PrimeSourceIntfc ps)
	{
		super(ps);
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
						final var last = ((TreeSet<Integer>)origIdxs).pollLast();
						var tmpSet = new TreeSet<Integer>(origIdxs);
						tmpSet.remove(last);
						pr.getPrimeBaseData().addPrimeBase(tmpSet, BaseTypes.PREFIX);
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
