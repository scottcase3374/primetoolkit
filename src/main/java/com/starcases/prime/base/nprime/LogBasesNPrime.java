package com.starcases.prime.base.nprime;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import picocli.CommandLine.Command;

/**
 * Provides logging of NPrime style base information.
 */
@SuppressWarnings({"PMD.LawOfDemeter"})
public class LogBasesNPrime extends AbstractLogBase
{
	/**
	 * default logger
	 */
	private static final Logger log = Logger.getLogger(LogBasesNPrime.class.getName());

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
		if (log.isLoggable(Level.INFO))
		{
			log.info(String.format("%nLogging NPrime%n"));
		}

		final var prIt = primeSrc.getPrimeRefStream(5L, false).iterator();

		prIt.forEachRemaining(pr ->
		{
			final var bmd = pr.getPrimeBaseData().getBaseMetadata(BaseTypes.NPRIME);
			if (bmd instanceof NPrimeBaseMetadata nprimemd)
			{
				final var counts = nprimemd.getCountForBaseIdx();
				final String itemCountsStr = counts.toString();

					// Handle "header" info for the current Prime
					PrimeToolKit.output(BaseTypes.NPRIME, "%nPrime [%d] %s %n",
														pr.getPrime(),
														itemCountsStr
														);
			}
			else
			{
				if (log.isLoggable(Level.SEVERE))
				{
					log.severe(String.format("Can't show bases for Prime [%d] index[%d] : invalid NPrimeBaseMetadata", pr.getPrime(), pr.getPrimeRefIdx()));
				}
			}
		});
	}
}
