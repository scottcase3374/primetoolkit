package com.starcases.prime.base.triples;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import picocli.CommandLine.Command;

/**
 *
 * Intent was to log the base type that consisted
 * specifically of 3 Prime bases that sum to each
 * Prime#. Not much differentiation between this and
 * LogBaseNPrime after a refactor.
 *
 */
public class LogBases3AllTriples  extends AbstractLogBase
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(LogBases3AllTriples.class.getName());

	/**
	 * constructor for logging base 3 triples
	 * @param primeSrc
	 */
	public LogBases3AllTriples(@NonNull final PrimeSourceIntfc primeSrc)
	{
		super(primeSrc);
	}

	/**
	 * output log info
	 */
	@SuppressWarnings({"PMD.AvoidFinalLocalVariable", "PMD.LawOfDemeter"})
	@Override
	@Command
	public void outputLogs()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("%nLogging triples%n"));
		}

		final var maxBasesInRow = 5;
		int [] idx = {5};
		primeSrc
			.getPrimeRefStream(5L, false)
			.forEach( pr ->
						{
							try
							{
								final var size = pr.getPrimeBaseData().getPrimeBases(BaseTypes.THREETRIPLE).size();
								PrimeToolKit.output(BaseTypes.THREETRIPLE, String.format("%nPrime [%d] idx[%d] #-bases[%d]%n",
										pr.getPrime(),
										idx[0]++,
										size
										));

									final long [] cnt = {0};
									final StringBuilder outputStr = new StringBuilder(150);
									outputStr.append('\t');
									pr.getPrimeBaseData().getPrimeBases(BaseTypes.THREETRIPLE)
											.stream()
											.<String>mapMulti((bs, consumer) ->
																{
																	cnt[0]++;
																	outputStr.append(
																	 	bs
																	 	.stream()
																	 	.map(BigInteger::toString)
																	 	.collect(Collectors.joining(",","[","]"))
																	 	);

																	if (cnt[0] < size)
																	{
																		outputStr.append(", ");
																	}

																	if (cnt[0] % maxBasesInRow == 0 || cnt[0] >= size)
																	{
																		consumer.accept(outputStr.toString());
																		outputStr.setLength(0);
																		outputStr.append('\t');
																	}
																}
													)
											.forEach(str -> PrimeToolKit.output(BaseTypes.THREETRIPLE, "%s", str));
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
					);
	}
}
