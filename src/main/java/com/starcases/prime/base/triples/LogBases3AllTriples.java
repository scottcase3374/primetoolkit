package com.starcases.prime.base.triples;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import picocli.CommandLine.Command;

/**
 *
 * Intent was to log the base type that consisted specifically of 3 Prime bases
 * that sum to each Prime#. Not much differentiation between this and LogBaseNPrime
 * after a refactor.
 *
 */
public class LogBases3AllTriples  extends AbstractLogBase
{
	private static final Logger log = Logger.getLogger(LogBases3AllTriples.class.getName());

	public LogBases3AllTriples(@NonNull PrimeSourceIntfc ps)
	{
		super(ps);
	}

	@Override
	@Command
	public void l()
	{
		if (log.isLoggable(Level.INFO))
			log.info(String.format("%nLogging triples%n"));

		// Get desired data
		ps.setActiveBaseId(BaseTypes.THREETRIPLE);

		final var maxBasesInRow = 5;
		int [] idx = {5};
		ps
			.getPrimeRefStream(false)
			.skip(5)
			.forEach( pr ->
						{
							try
							{
								var size = pr.getPrimeBaseData().getPrimeBaseIdxs(BaseTypes.THREETRIPLE).size();
								System.out.println(String.format("%nPrime [%d] idx[%d] #-bases[%d]%n",
										pr.getPrime(),
										idx[0]++,
										size
										));

									long [] cnt = {0};
									StringBuilder sb = new StringBuilder(150);
									sb.append("\t");
									pr.getPrimeBaseData().getPrimeBaseIdxs(BaseTypes.THREETRIPLE)
											.stream()
											.<String>mapMulti((bs, consumer) ->
																{
																	cnt[0]++;
																	sb.append(
																	 	bs
																	 	.stream()
																	 	.map(i -> ps.getPrime(i).get().toString())
																	 	.collect(Collectors.joining(",","[","]"))
																	 	);

																	if (cnt[0] < size)
																		sb.append(", ");

																	if (cnt[0] % maxBasesInRow == 0 || cnt[0] >= size)
																	{
																		consumer.accept(sb.toString());
																		sb.setLength(0);
																		sb.append("\t");
																	}
																}
													)
											.forEach(System.out::println);
							}
							catch(Exception e)
							{
								log.severe(String.format("Can't show bases for: %d exception:", pr.getPrime()));
								log.throwing(this.getClass().getName(), "log", e);
								e.printStackTrace();
							}
						}
					);
	}
}
