package com.starcases.prime.base.triples.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.impl.AbstractPrimeBaseLog;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.logging.PTKLogger;

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
public class LogBases3AllTriples  extends AbstractPrimeBaseLog
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
	@Override
	@Command
	public void outputLogs()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("%nLogging triples%n"));
		}

		final var maxBasesInRow = 5;
		final int [] idx = {5};
		primeSrc
			.getPrimeRefStream(5L, false)
			.forEach( primeRef ->
						{
							final var size = primeRef.getPrimeBaseData().getPrimeBases(BaseTypes.THREETRIPLE).size();
							PTKLogger.output(BaseTypes.THREETRIPLE,
												String.format("%nPrime [%d] idx[%d] #-bases[%d]",
													primeRef.getPrime(),
													idx[0]++,
													size
													));

								final long [] cnt = {0};
								final StringBuilder outputStr = new StringBuilder(150);
								primeRef
									.getPrimeBaseData()
									.getPrimeBases(BaseTypes.THREETRIPLE)
									.forEach( baseColl ->
												{
													cnt[0]++;
													outputStr.append(baseColl.makeString("[", ",", "]"));
													if (cnt[0] < size)
													{
														outputStr.append(", ");
													}

													if (cnt[0] % maxBasesInRow == 0 || cnt[0] >= size)
													{
														PTKLogger.output(BaseTypes.THREETRIPLE, "\t%s", outputStr);
														outputStr.setLength(0);
													}
												}
											);
						});
	}
}
