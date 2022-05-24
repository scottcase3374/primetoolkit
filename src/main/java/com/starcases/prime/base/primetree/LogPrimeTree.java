package com.starcases.prime.base.primetree;


import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;

/**
 * Provides a predefined algo that can log primes and
 * associated data about them as found in the
 * internal structures - in this case for the
 * tree/graph style representation.
 */
public class LogPrimeTree extends AbstractLogBase
{
	/**
	 * Default logger instance
	 */
	private static final Logger LOG = Logger.getLogger(LogPrimeTree.class.getName());

	/**
	 * Primary constructor
	 * @param primeSrc
	 */
	public LogPrimeTree(@NonNull final PrimeSourceIntfc primeSrc)
	{
		super(primeSrc);
	}

	/**
	 * Output the informaation regarding the base.
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public void outputLogs()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("LogPrimeTree log()%n"));
		}

		final var outputStr = new StringBuilder();

		final int [] itemIdx = {0};

		primeSrc.getPrimeRefStream(false)
		.<String>mapMulti((pr, consumer) ->
							{
								outputStr.append(String.format("Prime [%d] idx[%d] Tree: ", pr.getPrime(), itemIdx[0]));

								pr
								.getPrimeBaseData()
								.getPrimeBases(BaseTypes.PRIME_TREE)
								.iterator()
								.forEachRemaining( primeBases ->

														outputStr.append(
															primeBases
														 	.stream()
														 	.map(BigInteger::toString)
														 	.collect(Collectors.joining(",","[","]"))
														 	)

													);
								outputStr.append(String.format("%n"));
								consumer.accept(outputStr.toString());
								outputStr.setLength(0);
								itemIdx[0]++;

							}
				)
		.forEach(str -> PrimeToolKit.output(BaseTypes.PRIME_TREE, "%s", str));
	}
}

