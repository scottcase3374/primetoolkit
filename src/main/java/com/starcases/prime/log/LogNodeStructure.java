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
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(LogNodeStructure.class.getName());

	/**
	 * Constructor for logging node structures
	 * @param primeSrc
	 */
	public LogNodeStructure(@NonNull final PrimeSourceIntfc primeSrc)
	{
		super(primeSrc);
	}

	@SuppressWarnings({"PMD.LawOfDemeter"})
	@Override
	public void outputLogs()
	{
		LOG.info("LogNodeStructure l()");
		var idx = 0;
		final var prIt = primeSrc.getPrimeRefIter();
		while (prIt.hasNext())
		{
			final var primeRef = prIt.next();
			try
			{
				PrimeToolKit.output(String.format("%nPrime [%d] idx[%d]  %n",
						primeRef.getPrime(),
						idx++
						));

					final long [] cnt = {0};
					final var outputStr = new StringBuilder("\t");

					primeRef.getPrimeBaseData().getPrimeBases()
							.stream()
							.filter( p -> !p.contains(BigInteger.ZERO))
							.reduce((s1, s2) -> { s1.addAll(s2); return s1; })
							.stream()
							.<String>mapMulti((basePrimes, consumer) ->
												{
													outputStr.append(
													 	basePrimes
													 	.stream()
													 	.map(BigInteger::toString)
													 	.collect(Collectors.joining(",","[","]"))
													 	);

													if (cnt[0] % 5 == 0 || cnt[0] >= primeRef.getPrimeBaseData().getPrimeBases().get(0).size())
													{
														consumer.accept(outputStr.toString());
														outputStr.setLength(0);
														outputStr.append('\t');
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
					LOG.severe(String.format("Can't show bases for: %d exception:", primeRef.getPrime()));
					LOG.throwing(this.getClass().getName(), "l()", e);
				}
			}
		}
	}
}
