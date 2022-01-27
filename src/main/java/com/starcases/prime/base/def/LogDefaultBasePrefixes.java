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
	List<BitSet> prefixes = new ArrayList<>();
	List<BitSet> primes = new ArrayList<>();

	public LogDefaultBasePrefixes(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	@Override
	public void log()
	{
		System.out.println(String.format("%n"));

		int idx = 0;
		var prIt = ps.getPrimeRefIter();
		while (prIt.hasNext())
		{
			var pr = prIt.next();
			try
			{
				BitSet origBS = pr.getPrimeBaseData().getPrimeBaseIdxs().get(0);

				BitSet prefixBS = (BitSet)origBS.clone();
				prefixBS.clear(origBS.length()-1);
				int prefixIdx = prefixes.indexOf(prefixBS);
				if (prefixIdx == -1)
				{
					prefixes.add(prefixBS);
					BitSet p = new BitSet();
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
		}

		StringBuilder sb = new StringBuilder();

		int [] itemIdx = {0};

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

								sb.append(
										String.format(
											" Primes %s%n",
											primes.get(itemIdx[0]).stream().boxed().map(i2 -> ps.getPrime(i2).get().toString()).collect(Collectors.joining(",", "[", "]")) ));
								consumer.accept(sb.toString());
								sb.setLength(0);
								itemIdx[0]++;
							}
				)
		.forEach(System.out::println);

	}
}

