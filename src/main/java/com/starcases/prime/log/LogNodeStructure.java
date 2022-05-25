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
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("LogNodeStructure l()");
		}
		var idx = 0;
		final var prIt = primeSrc.getPrimeRefIter();
		final var outputStr = new StringBuilder("\t");
		while (prIt.hasNext())
		{
			final var primeRef = prIt.next();
			final long [] cnt = {0};
			outputStr.append(String.format("%nPrime [%d] idx[%d]  %n\t",
					primeRef.getPrime(),
					idx++
					));

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
					.forEach(str -> PrimeToolKit.output( "%s", str));
		}
	}
}
