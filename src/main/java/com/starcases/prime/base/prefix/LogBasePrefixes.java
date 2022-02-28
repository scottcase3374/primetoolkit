package com.starcases.prime.base.prefix;

import java.util.List;
import java.util.stream.Collectors;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public class LogBasePrefixes extends AbstractLogBase
{
	public LogBasePrefixes(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	@Override
	public void log()
	{
		log.info(String.format("LogBasePrefixes log()%n"));

		final var sb = new StringBuilder();

		final int [] itemIdx = {0};

		ps.getPrimeRefStream(preferParallel)
		.<String>mapMulti((pr, consumer) ->
							{
								List<Integer> primeIndexes = pr.getPrimeBaseData().getPrimeBaseIdxs(BaseTypes.PREFIX).get(0);

								sb.append(String.format("Prime [%d] Prefix: ", pr.getPrime()));
								sb.append(
									primeIndexes
								 	.stream()
								 	.map(i -> ps.getPrime(i).get().toString())
								 	.collect(Collectors.joining(",","[","]"))
								 	);

								sb.append(String.format("%n"));
								consumer.accept(sb.toString());
								sb.setLength(0);
								itemIdx[0]++;
							}
				)
		.forEach(System.out::println);
	}
}

