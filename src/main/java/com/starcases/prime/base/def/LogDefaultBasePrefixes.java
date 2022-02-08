package com.starcases.prime.base.def;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public class LogDefaultBasePrefixes extends AbstractLogBase
{
	private final List<BitSet> prefixes = new ArrayList<>();
	private final List<BitSet> primes = new ArrayList<>();

	public LogDefaultBasePrefixes(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	void generate()
	{
		final var prStream = ps.getPrimeRefStream(preferParallel);
		prStream.forEach(pr ->
				{
					try
					{
						final var origBS = pr.getPrimeBaseData().getPrimeBaseIdxs().get(0);

						final var prefixBS = (BitSet)origBS.clone();

						if (!origBS.isEmpty())
							prefixBS.clear(origBS.length()-1);

						final var prefixIdx = prefixes.indexOf(prefixBS);
						if (prefixIdx == -1)
						{
							prefixes.add(prefixBS);
							final var p = new BitSet();
							p.set(pr.getPrimeRefIdx());
							primes.add(p);
						}
						else
						{
							primes.get(prefixIdx).set(pr.getPrimeRefIdx());
						}
					}
					catch(Exception e)
					{
						log.severe(String.format("Can't show bases for: %d exception:", pr.getPrime()));
						log.throwing(this.getClass().getName(), "log", e);
						e.printStackTrace();
					}
				});
	}

	@Override
	public void log()
	{
		generate();

		System.out.println(String.format("%n"));

		final var sb = new StringBuilder();

		final int [] totalHandled = {0};
		final int [] itemIdx = {0};

		prefixes.stream()
		.<String>mapMulti((bs, consumer) ->
							{
								sb.append("Prefix: ");
								sb.append(
								 	bs
								 	.stream()
								 	.boxed()
								 	.map(i -> ps.getPrime(i).get().toString())
								 	.collect(Collectors.joining(",","[","]"))
								 	);
								final var localCnt = primes.get(itemIdx[0]).cardinality();
								totalHandled[0] += localCnt;
								sb.append(String.format("  count: [%d]", localCnt));
								sb.append(
										String.format(
											" Primes %s",
											primes.get(itemIdx[0]).stream().boxed().map(i2 -> ps.getPrime(i2).get().toString()).collect(Collectors.joining(",", "[", "]")) ));
								sb.append(String.format("%n"));
								consumer.accept(sb.toString());
								sb.setLength(0);
								itemIdx[0]++;
							}
				)
		.forEach(System.out::println);

		System.out.println("Total handled = " + totalHandled[0]);
	}
}

