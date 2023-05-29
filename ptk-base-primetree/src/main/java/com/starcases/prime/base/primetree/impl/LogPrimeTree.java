package com.starcases.prime.base.primetree.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.impl.PTKLogger;
import com.starcases.prime.logging.AbstractPrimeBaseLog;

import lombok.NonNull;

/**
 * Provides a predefined algo that can log primes and
 * associated data about them as found in the
 * internal structures - in this case for the
 * tree/graph style representation.
 */
class LogPrimeTree extends AbstractPrimeBaseLog
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
	 * Output the information regarding the base.
	 */
	@Override
	public void outputLogs()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("LogPrimeTree log()%n"));
		}

		final var outputStr = new StringBuilder();

		final int [] itemIdx = {0};

		primeSrc
			.getPrimeRefStream(false)
			.forEach( primeRef ->
							{
								PTKLogger.output(BaseTypes.PRIME_TREE, "%s", String.format("Prime [%d] idx[%d] Tree: ", primeRef.getPrime(), itemIdx[0]));

								primeRef
									.getPrimeBaseData()
									.getPrimeBases(BaseTypes.PRIME_TREE)
									.iterator()
									.forEachRemaining( primeBases -> primeBases.appendString(outputStr, "[", ",", "]"));
								PTKLogger.output(BaseTypes.PRIME_TREE, "\t%s%n", outputStr);
								outputStr.setLength(0);
								itemIdx[0]++;

							}
				);
	}
}

