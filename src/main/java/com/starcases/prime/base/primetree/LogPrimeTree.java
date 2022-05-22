package com.starcases.prime.base.primetree;


import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
	private static final Logger LOG = Logger.getLogger(LogPrimeTree.class.getName());

	public LogPrimeTree(@NonNull final PrimeSourceIntfc ps)
	{
		super(ps);
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public void l()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("LogPrimeTree log()%n"));
		}

		final var sb = new StringBuilder();

		final int [] itemIdx = {0};

		ps.getPrimeRefStream(false)
		.<String>mapMulti((pr, consumer) ->
							{
								sb.append(String.format("Prime [%d] idx[%d] Tree: ", pr.getPrime(), itemIdx[0]));

								pr
								.getPrimeBaseData()
								.getPrimeBases(BaseTypes.PRIME_TREE)
								.iterator()
								.forEachRemaining( primeBases ->

														sb.append(
															primeBases
														 	.stream()
														 	.map(BigInteger::toString)
														 	.collect(Collectors.joining(",","[","]"))
														 	)

													);
								sb.append(String.format("%n"));
								consumer.accept(sb.toString());
								sb.setLength(0);
								itemIdx[0]++;

							}
				)
		.forEach(System.out::println);
	}
}

