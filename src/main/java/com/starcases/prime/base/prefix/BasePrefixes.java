package com.starcases.prime.base.prefix;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.metrics.MetricMonitor;

import io.micrometer.core.instrument.LongTaskTimer;
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

	@Override
	protected void genBasesImpl()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("BasePrefixes genBases()");
		}

		LOG.fine("BasePrefixes - get stream");
		final var prStream = primeSrc.getPrimeRefStream(preferParallel);
		prStream.forEach(pr ->
				{
					if (LOG.isLoggable(Level.FINE))
					{
						LOG.fine("basePrefixes stream - for each: " + pr.getPrime());
					}

					final Optional<LongTaskTimer.Sample> timer = MetricMonitor.longTimer(BaseTypes.PREFIX);
					try
					{
						final var origBases = pr.getPrimeBaseData().getPrimeBases().get(0);
						if (LOG.isLoggable(Level.FINE))
						{
							LOG.fine("basePrefixes stream - add prime base: " + origBases);
						}
						pr.getPrimeBaseData().addPrimeBases(Lists.mutable.of(origBases), BaseTypes.PREFIX);
					}
					finally
					{
						timer.ifPresent( t -> t.stop());
					}
				});
	}
}
