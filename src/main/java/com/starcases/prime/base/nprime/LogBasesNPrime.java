package com.starcases.prime.base.nprime;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.logging.Logger;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import picocli.CommandLine.Command;

/*
* Need to actually save the n-Prime bases so logging actually pulls the content - current output
* is all generated during creation of the n-Prime bases and not here. Need to refactor base handling
* to accomplish this well.
*/
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
		final var maxBasesInRow = 5;

		// Get desired data
		ps.setActiveBaseId(BaseTypes.NPRIME);

		var prIt = ps.getPrimeRefStream(false).skip(5).iterator();
		while (prIt.hasNext())
		{
			var pr = prIt.next();
			try
			{
				// Handle "header" info for the current Prime - Prime value and the index of the Prime.
				System.out.println(String.format("%nPrime [%d] idx[%d]%n",
													pr.getPrime(),
													pr.getPrimeRefIdx()
													));

					final var sb = new StringBuilder("\t");

					// get list of counts for the target Prime indexes that we reduced to.
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
													// Prime starting at index 0 and incrementing index by 1.
													//
													// Remember, the reduction is reducing to a "prefix" list of the primes.
													//
													// Index into counts
													int [] primeIdxCntIdx = {0};

													sb.append(
													 	indexBitset
													 	.stream()
													 	.map(index ->
													 		String.format("base-Prime:[%s] count:[%s]",
													 				ps.getPrime(primeIdxCntIdx[0]).get().toString(),
													 				counts.size() > primeIdxCntIdx[0] ? counts.get(primeIdxCntIdx[0]).toString() : 0   ))
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
				log.severe(String.format("Can't show bases for Prime [%d] index[%d] exception:", pr.getPrime(), pr.getPrimeRefIdx()));
				log.throwing(this.getClass().getName(), "log", e);
				e.printStackTrace();
			}
		}
	}
}
