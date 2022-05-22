package com.starcases.prime.log;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.starcases.prime.PrimeToolKit;
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
	private static final Logger LOG = Logger.getLogger(LogNodeStructure.class.getName());

	public LogNodeStructure(@NonNull final PrimeSourceIntfc ps)
	{
		super(ps);
	}

	@SuppressWarnings({"PMD.LawOfDemeter"})
	@Override
	public void l()
	{
		LOG.info("LogNodeStructure l()");
		var idx = 0;
		final var prIt = ps.getPrimeRefIter();
		while (prIt.hasNext())
		{
			final var pr = prIt.next();
			try
			{
				PrimeToolKit.output(String.format("%nPrime [%d] idx[%d]  %n",
						pr.getPrime(),
						idx++
						));

					final long [] cnt = {0};
					final var sb = new StringBuilder("\t");

					pr.getPrimeBaseData().getPrimeBases()
							.stream()
							.filter( p -> !p.contains(BigInteger.ZERO))
							.reduce((s1, s2) -> { s1.addAll(s2); return s1; })
							.stream()
							.<String>mapMulti((basePrimes, consumer) ->
												{
													sb.append(
													 	basePrimes
													 	.stream()
													 	.map(BigInteger::toString)
													 	.collect(Collectors.joining(",","[","]"))
													 	);

													if (cnt[0] % 5 == 0 || cnt[0] >= pr.getPrimeBaseData().getPrimeBases().get(0).size())
													{
														consumer.accept(sb.toString());
														sb.setLength(0);
														sb.append('\t');
													}
													cnt[0]++;
												}
									)
							.forEach(System.out::println);
			}
			catch(final Exception e)
			{
				if (LOG.isLoggable(Level.SEVERE))
				{
					LOG.severe(String.format("Can't show bases for: %d exception:", pr.getPrime()));
					LOG.throwing(this.getClass().getName(), "l()", e);
				}
			}
		}
	}
}
