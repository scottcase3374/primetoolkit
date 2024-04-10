package com.starcases.prime.logging;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.NonNull;

/**
 *
 * Logs data about the primes without using the graph structure - instead
 * it just uses my internal api's and displays some of the available info that
 * can be provided.
 *
 */
public class LogNodeStructure extends AbstractPrimeBaseLog
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(LogNodeStructure.class.getName());

	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
	/**
	 * Constructor for logging node structures
	 * @param primeSrc
	 */
	public LogNodeStructure(@NonNull final PrimeSourceIntfc primeSrc)
	{
		super(primeSrc);
	}

	@Override
	public void outputLogs()
	{
		if (LOG.isLoggable(Level.FINE))
		{
			LOG.fine("LogNodeStructure l()");
		}
		final int [] idx = {0};

		final var primeIt = primeSrc.getPrimeRefIter();
		primeIt.forEachRemaining(
				primeRef ->
					{
						statusHandler.output(String.format("%nPrime [%d] idx[%d]",
								primeRef.getPrime(),
								idx[0]++
								));

						statusHandler.output( "\t%s", Arrays.toString(primeRef.getPrimeBases()));
					});
	}
}
