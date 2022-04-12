package com.starcases.prime.base.nprime;

import java.math.BigInteger;
import java.util.stream.Collectors;

import org.eclipse.collections.api.bag.sorted.MutableSortedBag;

import java.util.logging.Logger;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import picocli.CommandLine.Command;

public class LogBasesNPrime extends AbstractLogBase
{
	private static final Logger log = Logger.getLogger(LogBasesNPrime.class.getName());

	public LogBasesNPrime(@NonNull PrimeSourceIntfc ps)
	{
		super(ps);
	}

	@Override
	@Command
	public void l()
	{
		// Get desired data
		ps.setActiveBaseId(BaseTypes.NPRIME);

		var prIt = ps.getPrimeRefStream(5L, false).iterator();
		while (prIt.hasNext())
		{
			var pr = prIt.next();
			try
			{
				// Handle "header" info for the current Prime - Prime value and the index of the Prime.
				System.out.println(String.format("%nPrime [%d] %n",
													pr.getPrime()
													));

				MutableSortedBag<BigInteger> counts;
				var bmd = pr.getPrimeBaseData().getBaseMetadata();
				if (bmd instanceof NPrimeBaseMetadata)
				{
					var nprimemd = (NPrimeBaseMetadata)bmd;
					counts = nprimemd.getCountForBaseIdx();
				}
				else
				{
					throw new IllegalArgumentException("Unexpected value: " + bmd);
				}

//              // Java 17 / 18 Preview feature
//				// get list of counts for the target Prime indexes that we reduced to.
//				final MutableSortedBag<BigInteger> counts =
//						switch(pr.getPrimeBaseData().getBaseMetadata())
//						{
//						case NPrimeBaseMetadata npbmd -> npbmd.getCountForBaseIdx();
//						default -> throw new IllegalArgumentException("Unexpected value: " + pr.getPrimeBaseData().getBaseMetadata());
//						};

				String s =
					counts
					.distinct()
					.stream()
					.map(base ->
							// the reduction is reducing to a "prefix" list of the primes.
							 String.format("base-Prime:[%d] count:[%d] ",
						 				base,
						 				counts.occurrencesOf(base))
							)
					.collect(Collectors.joining(",","[","]"));

				System.out.println(s);
			}
			catch(Exception e)
			{
				log.severe(String.format("Can't show bases for Prime [%d] index[%d] exception:", pr.getPrime(), pr.getPrimeRefIdx()));
				log.throwing(this.getClass().getName(), "log", e);
				e.printStackTrace();
			}
		}
	}
}
