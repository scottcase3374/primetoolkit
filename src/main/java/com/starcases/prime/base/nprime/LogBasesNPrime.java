package com.starcases.prime.base.nprime;

import java.util.stream.Collectors;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

/*
* Need to actually save the n-prime bases so logging actually pulls the content - current output
* is all generated during creation of the n-prime bases and not here. Need to refactor base handling
* to accomplish this well.
*/
@Log
public class LogBasesNPrime extends AbstractLogBase
{
	public LogBasesNPrime(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	@Override
	@Command
	public void log()
	{
		// Get desired data
		ps.setActiveBaseId(BaseTypes.NPRIME);

		int idx = 0;
		var prIt = ps.getPrimeRefIter();
		while (prIt.hasNext())
		{
			var pr = prIt.next();
			try
			{
				long size = pr.getPrimeBaseData().getPrimeBaseIdxs(BaseTypes.NPRIME).size();
				System.out.println(String.format("%nPrime [%d] idx[%d] #-bases[%d]%n",
						pr.getPrime(),
						idx++,
						size
						));

					long [] cnt = {0};
					StringBuilder sb = new StringBuilder("\t");

					pr.getPrimeBaseData().getPrimeBaseIdxs(BaseTypes.NPRIME)
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

													if (cnt[0] % 5 == 0 || cnt[0] >= size)
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
