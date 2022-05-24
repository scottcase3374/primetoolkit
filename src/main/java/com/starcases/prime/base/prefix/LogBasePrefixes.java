package com.starcases.prime.base.prefix;


import java.math.BigInteger;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;

/**
 * Logs prefix style base information.
 */
public class LogBasePrefixes extends AbstractLogBase
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(LogBasePrefixes.class.getName());

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
	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public void outputLogs()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("LogBasePrefixes %n"));
		}

		final var outputStr = new StringBuilder();

		final int [] itemIdx = {0};

		primeSrc.getPrimeRefStream(preferParallel)
		.<String>mapMulti((pr, consumer) ->
							{
								final Set<BigInteger> primeBases = pr.getPrimeBaseData().getPrimeBases(BaseTypes.PREFIX).get(0);

								outputStr.append(String.format("Prime [%d] Prefix: ", pr.getPrime()));
								outputStr.append(
									primeBases
								 	.stream()
								 	.map(BigInteger::toString)
								 	.collect(Collectors.joining(",","[","]"))
								 	);

								outputStr.append(String.format("%n"));
								consumer.accept(outputStr.toString());
								outputStr.setLength(0);
								itemIdx[0]++;
							}
				)
		.forEach(str -> PrimeToolKit.output(BaseTypes.PREFIX, "%s", str));
	}
}

