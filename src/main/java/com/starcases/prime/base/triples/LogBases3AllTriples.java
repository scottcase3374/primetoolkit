package com.starcases.prime.base.triples;

import java.math.BigInteger;
import java.util.stream.Collectors;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

/**
 *
 * Intent was to log the base type that consisted specifically of 3 Prime bases
 * that sum to each Prime#. Not much differentiation between this and LogBaseNPrime
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

		var idx = 5;
		var prIt = ps.getPrimeRefIter();

		// Primes under 11 don't have a representation consisting of 3 primes summed.
		while (prIt.hasNext() && !prIt.next().getPrime().equals(BigInteger.valueOf(7L)));

		final var maxBasesInRow = 5;

		while (prIt.hasNext())
		{
			var pr = prIt.next();
			try
			{
				var size = pr.getPrimeBaseData().getPrimeBaseIdxs(BaseTypes.THREETRIPLE).size();
				System.out.println(String.format("%nPrime [%d] idx[%d] #-bases[%d]%n",
						pr.getPrime(),
						idx++,
						size
						));

					long [] cnt = {0};
					StringBuilder sb = new StringBuilder("\t");

					pr.getPrimeBaseData().getPrimeBaseIdxs(BaseTypes.THREETRIPLE)
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
	}
}
