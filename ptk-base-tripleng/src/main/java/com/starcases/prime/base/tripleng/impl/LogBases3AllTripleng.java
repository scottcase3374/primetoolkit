package com.starcases.prime.base.tripleng.impl;

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
 *
 * Intent was to log the base type that consisted
 * specifically of 3 Prime bases that sum to each
 * Prime#. Not much differentiation between this and
 * LogBaseNPrime after a refactor.
 *
 */
class LogBases3AllTripleng  extends AbstractPrimeBaseLog
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(LogBases3AllTripleng.class.getName());

	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
	/**
	 * constructor for logging base 3 triples
	 * @param primeSrc
	 */
	public LogBases3AllTripleng(@NonNull final PrimeSourceIntfc primeSrc)
	{
		super(primeSrc);
	}

	/**
	 * output log info
	 */
	@Override
	public void outputLogs()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("%nLogging triples%n"));
		}

		final var maxBasesInRow = 5;
		final int [] idx = {5};
		primeSrc
			.getPrimeRefStream(5L, false)
			.forEach( primeRef ->

				Optional.ofNullable(primeRef.getPrimeBaseData())
					.ifPresent(pb ->
					{
						final var size = pb.getPrimeBases(TriplengBaseType.TRIPLENG).size();
						statusHandler.output(TriplengBaseType.TRIPLENG,
							String.format("%nPrime [%d] idx[%d] #-bases[%d]",
								primeRef.getPrime(),
								idx[0]++,
								size
								));

						final long [] cnt = {0};
						final StringBuilder outputStr = new StringBuilder(150);
						primeRef
							.getPrimeBaseData()
							.getPrimeBases(TriplengBaseType.TRIPLENG)
							.forEach( baseColl ->
										{
											cnt[0]++;
											outputStr.append(baseColl.makeString("[", ",", "]"));
											if (cnt[0] < size)
											{
												outputStr.append(", ");
											}

											if (cnt[0] % maxBasesInRow == 0 || cnt[0] >= size)
											{
												statusHandler.output(TriplengBaseType.TRIPLENG, "\t%s", outputStr);
												outputStr.setLength(0);
											}
										}
									);
								})
						);
	}
}
