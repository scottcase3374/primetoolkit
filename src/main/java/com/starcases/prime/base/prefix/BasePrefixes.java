package com.starcases.prime.base.prefix;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.factory.Lists;

import com.codahale.metrics.Timer;
import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.metrics.MetricMonitor;

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

		MetricMonitor.addTimer(BaseTypes.PREFIX,"Gen Prefix");

		LOG.fine("BasePrefixes - get stream");
		final var prStream = primeSrc.getPrimeRefStream(preferParallel);
		prStream.forEach(pr ->
				{
					LOG.fine("basePrefixes stream - for each: " + pr.getPrime());
					try (Timer.Context context = MetricMonitor.time(BaseTypes.PREFIX).orElse(null))
					{
						final var origBases = pr.getPrimeBaseData().getPrimeBases().get(0);
						LOG.fine("basePrefixes stream - add prime base: " + origBases);
						pr.getPrimeBaseData().addPrimeBases(Lists.mutable.of(origBases), BaseTypes.PREFIX);
					}
				});
	}
}
