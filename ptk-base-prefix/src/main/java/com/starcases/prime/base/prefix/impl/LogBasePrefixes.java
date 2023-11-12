package com.starcases.prime.base.prefix.impl;

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
 * Logs prefix style base information.
 */
class LogBasePrefixes extends AbstractPrimeBaseLog
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(LogBasePrefixes.class.getName());

	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
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
									Optional.ofNullable(  primeRef.getPrimeBaseData())
										.ifPresent(bd ->
											Optional.ofNullable(bd.getPrimeBases(PrefixBaseType.PREFIX))
											.ifPresent(bd1 ->
													bd1.forEach(
															primeBases ->
															{
																outputStr.append(String.format("Prime [%d] Prefix: \t", primeRef.getPrime()));

																primeBases.appendString(outputStr, "[", ",", "]");

																statusHandler.output(PrefixBaseType.PREFIX, "%s%n", outputStr);
																outputStr.setLength(0);
																itemIdx[0]++;
															}))));
	}
}

