package com.starcases.prime.base.prefix;

import java.math.BigInteger;
import java.util.logging.Logger;

import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;

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
	public void genBases(boolean trackGenTime)
	{
		log.info("BasePrefixes genBases()");

		final var prStream = ps.getPrimeRefStream(preferParallel);
		if (trackGenTime)
			event(true);

		prStream.forEach(pr ->
				{
					try
					{
						final var origBases = pr.getPrimeBaseData().getPrimeBases().get(0);

						// We don't include the Pn-1 idx in prefix list
						final var last = ((MutableSortedSet<BigInteger>)origBases).getLastOptional();
						MutableSortedSet<BigInteger> tmpSet = TreeSortedSet.newSet(origBases);
						last.ifPresent(l -> tmpSet.remove(l));
						pr.getPrimeBaseData().addPrimeBases(tmpSet, BaseTypes.PREFIX);
					}
					catch(Exception e)
					{
						log.severe(String.format("Can't show bases for: %d exception:", pr.getPrime()));
						log.throwing(this.getClass().getName(), "log", e);
						e.printStackTrace();
					}
				});

		if (trackGenTime)
			event(false);
	}
}
