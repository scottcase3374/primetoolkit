package com.starcases.prime.base.nprime;

import java.util.Collections;
import java.util.stream.Collectors;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

/*
* Need to actually save the n-prime bases so logging actually pulls the content - current output
* is all generated during creation of the n-prime bases and not here. Need to refactor base handling
* to accomplish this well.
*/
@Log
public class LogBasesNPrime extends AbstractLogBase
{
	public LogBasesNPrime(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	@Override
	@Command
	public void log()
	{
		final int maxBasesInRow = 5;

		// Get desired data
		ps.setActiveBaseId(BaseTypes.NPRIME);

		var prIt = ps.getPrimeRefIter();
		while (prIt.hasNext())
		{
			var pr = prIt.next();
			try
			{
				// Handle "header" info for the current prime - prime value and the index of the prime.
				System.out.println(String.format("%nPrime [%d] idx[%d]%n",
													pr.getPrime(),
													pr.getPrimeRefIdx()
													));

					final var sb = new StringBuilder("\t");

					// get list of counts for the target prime indexes that we reduced to.
					final var counts =
							switch(pr.getPrimeBaseData().getBaseMetadata())
							{
							case NPrimeBaseMetadata npbmd -> npbmd.getCountForBaseIdx();
							default -> Collections.emptyList();
							};

					pr.getPrimeBaseData()
							.getPrimeBaseIdxs(BaseTypes.NPRIME)
							.stream()
							.<String>mapMulti((indexBitset, consumer) ->
									{
													// No direct correlation between index bit set 'index' value in the map() below
													// and the list of counts. Each item in the count-list is simply associated with the next
													// prime starting at index 0 and incrementing index by 1.
													//
													// Remember, the reduction is reducing to a "prefix" list of the primes.
													int [] primeIdxCntIdx = {0};

													sb.append(
													 	indexBitset
													 	.stream()
													 	.boxed()
													 	.map(index -> String.format("base-prime-%d count:[%d]", ps.getPrime(primeIdxCntIdx[0]).get(), counts.get(primeIdxCntIdx[0]++)) )
													 	.collect(Collectors.joining(",","[","]"))
													 	);

													if (primeIdxCntIdx[0] < counts.size())
														sb.append(", ");

													if (primeIdxCntIdx[0] % maxBasesInRow == 0 || primeIdxCntIdx[0] >= counts.size())
													{
														consumer.accept(sb.toString());
														sb.setLength(0);
														sb.append("\t");
													}
												}
									)
							.forEach(System.out::println);
			}
			catch(Exception e)
			{
				log.severe(String.format("Can't show bases for prime [%d] index[%d] exception:", pr.getPrime(), pr.getPrimeRefIdx()));
				log.throwing(this.getClass().getName(), "log", e);
				e.printStackTrace();
			}
		}
	}
}
