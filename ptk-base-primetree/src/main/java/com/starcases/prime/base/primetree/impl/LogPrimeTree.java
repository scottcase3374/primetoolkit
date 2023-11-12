package com.starcases.prime.base.primetree.impl;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.logging.AbstractPrimeBaseLog;
import com.starcases.prime.service.impl.SvcLoader;

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

	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
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
								statusHandler.output(PrimeTreeBaseType.PRIME_TREE, "%s", String.format("Prime [%d] idx[%d] Tree: ", primeRef.getPrime(), itemIdx[0]));

								Optional.ofNullable(primeRef.getPrimeBaseData())
									.ifPresent( bd ->
										bd.getPrimeBases(PrimeTreeBaseType.PRIME_TREE)
										.iterator()
										.forEachRemaining( primeBases -> primeBases.appendString(outputStr, "[", ",", "]")));

								statusHandler.output(PrimeTreeBaseType.PRIME_TREE, "\t%s%n", outputStr);
								outputStr.setLength(0);
								itemIdx[0]++;

							}
				);
	}
}

