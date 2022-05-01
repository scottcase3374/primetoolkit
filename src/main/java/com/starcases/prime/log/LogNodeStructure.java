package com.starcases.prime.log;

import java.math.BigInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

/**
 *
 * Logs data about the primes without using the graph structure - instead
 * it just uses my internal api's and displays some of the available info that
 * can be provided.
 *
 */
public class LogNodeStructure extends AbstractLogBase
{
	private static final Logger log = Logger.getLogger(LogNodeStructure.class.getName());

	public LogNodeStructure(@NonNull PrimeSourceIntfc ps)
	{
		super(ps);
	}

	@Override
	public void l()
	{
		log.info("LogNodeStructure log()");
		var idx = 0;
		final var prIt = ps.getPrimeRefIter();
		while (prIt.hasNext())
		{
			final var pr = prIt.next();
			try
			{
				System.out.println(String.format("%nPrime [%d] idx[%d]  %n",
						pr.getPrime(),
						idx++
						));

					final long [] cnt = {0};
					final var sb = new StringBuilder("\t");

					pr.getPrimeBaseData().getPrimeBases()
							.stream()
							.peek(c -> { System.out.println(String.format("set orig: %s", c.stream().map(b -> b.toString()).collect(Collectors.joining(",")))); } )
							.filter( p -> !p.contains(BigInteger.ZERO))
							.reduce((s1, s2) -> { s1.addAll(s2); return s1; })

							.stream()
							.peek(c -> { System.out.println(String.format("set reduced: %s", c.stream().map(b -> b.toString()).collect(Collectors.joining(",")))); } )
							.<String>mapMulti((basePrimes, consumer) ->
												{
													sb.append(
													 	basePrimes
													 	.stream()
													 	.map(basePrime -> basePrime.toString())
													 	.collect(Collectors.joining(",","[","]"))
													 	);

													if (cnt[0] % 5 == 0 || cnt[0] >= pr.getPrimeBaseData().getPrimeBases().get(0).size())
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
