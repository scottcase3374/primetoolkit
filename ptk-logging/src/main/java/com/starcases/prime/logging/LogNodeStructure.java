package com.starcases.prime.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.impl.PTKLogger;

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
						// A good question is the effect of moving this out of
						// this block and into the outer block of the method.
						// That implies same string builder is used and
						// any memory allocations made would remain for each
						// of the iterations that follow.  Would that
						// improve speed by reducing memory allocation or
						// possible have little effect.  May reduce garbage
						// generated but maybe other consequences.
						final var outputStr = new StringBuilder("\t");

						final long [] cnt = {0};
						PTKLogger.output(String.format("%nPrime [%d] idx[%d]",
								primeRef.getPrime(),
								idx[0]++
								));

						primeRef
							.getPrimeBaseData()
							.getPrimeBases()
							.collect(p -> p.makeString("[", ",", "]"))
							.forEach( s ->
										{
											cnt[0]++;
											outputStr.append(s);
											if (cnt[0] % 5 == 0 || cnt[0] >= primeRef.getPrimeBaseData().getPrimeBases().get(0).size())
											{
												PTKLogger.output( "\t%s", outputStr);
												outputStr.setLength(0);
											}
											else
											{
												outputStr.append(',');
											}
										}
									);
					});
	}
}
