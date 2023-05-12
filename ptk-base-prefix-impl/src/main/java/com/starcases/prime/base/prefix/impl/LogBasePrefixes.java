package com.starcases.prime.base.prefix.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.impl.AbstractPrimeBaseLog;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.logging.PTKLogger;

import lombok.NonNull;

/**
 * Logs prefix style base information.
 */
public class LogBasePrefixes extends AbstractPrimeBaseLog
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
									final ImmutableLongCollection primeBases = primeRef.getPrimeBaseData().getPrimeBases(BaseTypes.PREFIX).get(0);

									outputStr.append(String.format("Prime [%d] Prefix: \t", primeRef.getPrime()));

									primeBases.appendString(outputStr, "[", ",", "]");

									PTKLogger.output(BaseTypes.PREFIX, "%s%n", outputStr);
									outputStr.setLength(0);
									itemIdx[0]++;
								}
					);
	}
}

