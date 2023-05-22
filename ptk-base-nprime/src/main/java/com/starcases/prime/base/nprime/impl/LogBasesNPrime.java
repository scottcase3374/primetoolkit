package com.starcases.prime.base.nprime.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.bag.primitive.ImmutableLongBag;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.impl.PTKLogger;
import com.starcases.prime.logging.AbstractPrimeBaseLog;

import lombok.NonNull;
import picocli.CommandLine.Command;

/**
 * Provides logging of NPrime style base information.
 */
public class LogBasesNPrime extends AbstractPrimeBaseLog
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(LogBasesNPrime.class.getName());

	/**
	 * Constructor for logging of NPrime base
	 * @param primeSrc
	 */
	public LogBasesNPrime(@NonNull final PrimeSourceIntfc primeSrc)
	{
		super(primeSrc);
	}

	@Override
	@Command
	public void outputLogs()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("%nLogging NPrime%n"));
		}

		final var prIt = primeSrc.getPrimeRefStream(5L, false).iterator();


		prIt.forEachRemaining(this::logData);
	}

	private void logData(final PrimeRefIntfc primeRef)
	{
		final var bmd = primeRef.getPrimeBaseData().getBaseMetadata(BaseTypes.NPRIME);
		if (bmd instanceof NPrimeBaseMetadata nprimemd)
		{
			final ImmutableLongBag counts = nprimemd.getCountForBaseIdx();
			final String itemCountsStr = counts.toStringOfItemToCount();

			// Handle "header" info for the current Prime
			PTKLogger.output(BaseTypes.NPRIME, "%nPrime [%d] %s%n",
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
	}
}
