package com.starcases.prime.log;

import java.util.stream.Collectors;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

/**
 *
 * Intent was to log the base type that consisted specifically of 3 prime bases
 * that sum to each prime#. Not much differentiation between this and LogBaseNPrime
 * after a refactor.
 *
 */
@Log
public class LogBases3AllTriples  extends AbstractLogBase
{
	public LogBases3AllTriples(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	@Override
	@Command
	public void log()
	{
		// Get desired data
		ps.setActiveBaseId(BaseTypes.THREETRIPLE);

		int idx = 0;
		var prIt = ps.getPrimeRefIter();
		while (prIt.hasNext())
		{
			var pr = prIt.next();
			try
			{
				long size = pr.getPrimeBaseIdxs().size();
				System.out.println(String.format("%nPrime [%d] idx[%d] #-bases[%d]%n",
						pr.getPrime(),
						idx++,
						size
						));

					long [] cnt = {0};
					StringBuilder sb = new StringBuilder("\t");

					pr.getPrimeBaseIdxs()
							.stream()
							.<String>mapMulti((bs, consumer) ->
												{
													cnt[0]++;
													sb.append(
													 	bs
													 	.stream()
													 	.boxed()
													 	.map(i -> ps.getPrime(i).get().toString())
													 	.collect(Collectors.joining(",","[","]"))
													 	);

													if (cnt[0] < size)
														sb.append(", ");

													if (cnt[0] % 5 == 0)
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
	}
}
