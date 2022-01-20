package com.starcases.prime.log;

import java.util.stream.Collectors;

import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;

/**
 *
 * Logs data about the primes without using the graph structure - instead
 * it just uses my internal api's and displays some of the available info that
 * can be provided.
 *
 */
@Log
public class LogNodeStructure extends AbstractLogBase
{
	public LogNodeStructure(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	@Override
	public void log()
	{
		int idx = 0;
		var prIt = ps.getPrimeRefIter();
		while (prIt.hasNext())
		{
			var pr = prIt.next();
			try
			{
				long size = pr.getPrimeBaseData().getPrimeBaseIdxs().size();
				System.out.println(String.format("%nPrime [%d] idx[%d] #-bases[%d]%n",
						pr.getPrime(),
						idx++,
						size
						));

					long [] cnt = {0};
					StringBuilder sb = new StringBuilder("\t");

					pr.getPrimeBaseData().getPrimeBaseIdxs()
							.stream()
							.<String>mapMulti((bs, consumer) ->
												{
													cnt[0]++;
													sb.append(
													 	bs
													 	.stream()
													 	.boxed()
													 	.map(i -> ps.getPrime(i).get().toString())
													 	.collect(Collectors.joining(",","[","]"))
													 	);

													if (cnt[0] < size)
														sb.append(", ");

													if (cnt[0] % 5 == 0 || cnt[0] >= size)
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
				log.severe(String.format("Can't show bases for: %d exception:", pr.getPrime()));
				log.throwing(this.getClass().getName(), "log", e);
				e.printStackTrace();
			}
		}
	}
}
