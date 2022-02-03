package com.starcases.prime.base.def;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public class LogDefaultBasePrefixes extends AbstractLogBase
{
	static Comparator<BitSet> bsComparator = (bs1, bs2) ->
		{
			var bs1it = bs1.stream().iterator();
			var bs2it = bs2.stream().iterator();

			int diff = 0;
			while (bs1it.hasNext() && bs2it.hasNext() && diff == 0)
			{
				var bs1int = bs1it.next();
				var bs2int = bs2it.next();
				diff = bs1int - bs2int;
			}

			if (diff == 0 && bs1it.hasNext())
			{
				diff = 1;
			}
			return diff;

		} ;

	List<BitSet> prefixes = new ArrayList<>();
	List<BitSet> primes = new ArrayList<>();

	public LogDefaultBasePrefixes(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	void generate()
	{
		var prIt = ps.getPrimeRefIter();
		while (prIt.hasNext())
		{
			var pr = prIt.next();
			try
			{
				BitSet origBS = pr.getPrimeBaseData().getPrimeBaseIdxs().get(0);

				BitSet prefixBS = (BitSet)origBS.clone();

				if (!origBS.isEmpty())
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
	}

	@Override
	public void log()
	{
		generate();

		System.out.println(String.format("%n"));

		StringBuilder sb = new StringBuilder();

		int [] totalHandled = {0};
		int [] itemIdx = {0};
		//prefixes.sort(bsComparator);
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
								var localCnt = primes.get(itemIdx[0]).cardinality();
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

