package com.starcases.prime.base.prefix.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.common.api.PTKLogger;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.logging.AbstractPrimeBaseLog;

import lombok.NonNull;

/**
 * Logs prefix style base information.
 */
class LogBasePrefixes extends AbstractPrimeBaseLog
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(LogBasePrefixes.class.getName());

	/**
	 * constructor for logging of base prefixes
	 * @param primeSrc
	 */
	public LogBasePrefixes(@NonNull final PrimeSourceIntfc primeSrc)
	{
		super(primeSrc);
	}

	/**
	 * Output log info
	 */
	@Override
	public void outputLogs()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("LogBasePrefixes %n"));
		}

		final var outputStr = new StringBuilder();

		final int [] itemIdx = {0};

		primeSrc
			.getPrimeRefStream(false)
			.forEach( primeRef ->
								{
									primeRef
									.getPrimeBaseData()
									.getPrimeBases(PrefixBaseType.PREFIX)
									.forEach(
											primeBases ->
											{

												outputStr.append(String.format("Prime [%d] Prefix: \t", primeRef.getPrime()));

												primeBases.appendString(outputStr, "[", ",", "]");

												PTKLogger.output(PrefixBaseType.PREFIX, "%s%n", outputStr);
												outputStr.setLength(0);
												itemIdx[0]++;
											});
								}
					);
	}
}

