package com.starcases.prime.base.prefixtree;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;

public class LogBasePrefixTree extends AbstractLogBase
{
	private static final Logger log = Logger.getLogger(LogBasePrefixTree.class.getName());

	public LogBasePrefixTree(@NonNull PrimeSourceIntfc ps)
	{
		super(ps);
	}

	@Override
	public void l()
	{
		log.info(String.format("LogBasePrefixTree log()%n"));

		final var sb = new StringBuilder();

		final int [] itemIdx = {0};

		ps.getPrimeRefStream(false)
		.<String>mapMulti((pr, consumer) ->
							{
								pr
									.getPrimeBaseData()
									.getPrimeBases(BaseTypes.PREFIX_TREE)
									.iterator()
									.forEachRemaining( primeBases ->
														{
															sb.append(String.format("Prime [%d] Prefix: ", pr.getPrime()));
															sb.append(
																primeBases
															 	.stream()
															 	.map(i -> i.toString())
															 	.collect(Collectors.joining(",","[","]"))
															 	);

															sb.append(String.format("%n"));
															consumer.accept(sb.toString());
															sb.setLength(0);
															itemIdx[0]++;
														});
							}
				)
		.forEach(System.out::println);
	}
}

