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
		log.info("LogNodeStructure log()");
		var idx = 0;
		final var prIt = ps.getPrimeRefIter();
		while (prIt.hasNext())
		{
			final var pr = prIt.next();
			try
			{
				final var size = pr.getPrimeBaseData().getPrimeBaseIdxs().size();
				System.out.println(String.format("%nPrime [%d] idx[%d] base-counts[%d] base0-size[%d] %n",
						pr.getPrime(),
						idx++,
						size,
						pr.getPrimeBaseData().getPrimeBaseIdxs().get(0).size()
						));

					final long [] cnt = {0};
					final var sb = new StringBuilder("\t");

					pr.getPrimeBaseData().getPrimeBaseIdxs()
							.stream()
							.<String>mapMulti((idxs, consumer) ->
												{

													sb.append(
													 	idxs
													 	.stream()
													 	.map(i -> ps.getPrime(i).get().toString())
													 	.collect(Collectors.joining(",","[","]"))
													 	);

													if (cnt[0] < size)
														sb.append(", ");

													if (cnt[0] % 5 == 0 || cnt[0] >= pr.getPrimeBaseData().getPrimeBaseIdxs().get(0).size())
													{
														consumer.accept(sb.toString());
														sb.setLength(0);
														sb.append("\t");
													}
													cnt[0]++;
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
