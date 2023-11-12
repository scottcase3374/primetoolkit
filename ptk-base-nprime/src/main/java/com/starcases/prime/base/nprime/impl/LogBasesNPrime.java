package com.starcases.prime.base.nprime.impl;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.bag.primitive.ImmutableLongBag;
import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.logging.AbstractPrimeBaseLog;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.NonNull;

/**
 * Provides logging of NPrime style base information.
 */
class LogBasesNPrime extends AbstractPrimeBaseLog
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(LogBasesNPrime.class.getName());

	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
	/**
	 * Constructor for logging of NPrime base
	 * @param primeSrc
	 */
	public LogBasesNPrime(@NonNull final PrimeSourceIntfc primeSrc)
	{
		super(primeSrc);
	}

	@Override
	public void outputLogs()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("%nLogging NPrime%n"));
		}

		final var prIt = primeSrc.getPrimeRefStream(5L, false).iterator();


		prIt.forEachRemaining(this::logData);
	}

	private void logData(@NonNull final PrimeRefIntfc primeRef)
	{
		final var bdOpt = Optional.ofNullable(primeRef.getPrimeBaseData());
		bdOpt.ifPresent(bd ->
		{
			final var bmdOpt = bd.getBaseMetadata(NPrimeBaseType.NPRIME);

			if (bmdOpt != null && bmdOpt instanceof NPrimeBaseMetadata nprimemd)
			{
				final ImmutableLongBag counts = nprimemd.getCountForBaseIdx();
				final String itemCountsStr = counts.toStringOfItemToCount();

				// Handle "header" info for the current Prime
				statusHandler.output(NPrimeBaseType.NPRIME, "%nPrime [%d] %s%n",
													primeRef.getPrime(),
													itemCountsStr
													);
			}
			else
			{
				if (LOG.isLoggable(Level.SEVERE))
				{
					LOG.severe(String.format("Can't show bases for Prime [%d] index[%d] : invalid NPrimeBaseMetadata", primeRef.getPrime(), primeRef.getPrimeRefIdx()));
				}
			}
		});
	}
}
