package com.starcases.prime.base.prefix;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

/**
 * Produces prefix list for each prime
 */
public class BasePrefixes extends AbstractPrimeBaseGenerator
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(BasePrefixes.class.getName());

	/**
	 * constructor for creation of Base prefixes.
	 * @param primeSrc
	 */
	public BasePrefixes(@NonNull final PrimeSourceIntfc primeSrc)
	{
		super(primeSrc);
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	protected void genBasesImpl()
	{
		LOG.info("BasePrefixes genBases()");

		final var prStream = primeSrc.getPrimeRefStream(preferParallel);
		prStream.forEach(pr ->
				{
					try
					{
						final var origBases = pr.getPrimeBaseData().getPrimeBases().get(0);
						pr.getPrimeBaseData().addPrimeBases(List.of(origBases), BaseTypes.PREFIX);
					}
					catch(final Exception e)
					{
						if (LOG.isLoggable(Level.SEVERE))
						{
							LOG.severe(String.format("Can't show bases for: %d exception:", pr.getPrime()));
							LOG.throwing(this.getClass().getName(), "genBasesImpl()", e);
						}
					}
				});
	}
}
