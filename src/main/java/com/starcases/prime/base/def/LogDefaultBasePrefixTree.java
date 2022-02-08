package com.starcases.prime.base.def;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;

class Tree
{
	@Getter
	private final AtomicReference<BigInteger> prefixPrime = new AtomicReference<>();

	@Getter
	private final Map<BigInteger, Tree> next;

	@Getter
	private final List<BigInteger> sourcePrimes = Collections.synchronizedList(new ArrayList<>());

	Tree(@NonNull final BigInteger prefixPrime, final Map<BigInteger, Tree> next )
	{
		this.prefixPrime.set(prefixPrime);
		this.next = next;
	}
}

@Log
public class LogDefaultBasePrefixTree extends AbstractLogBase
{
	final Map<BigInteger, Tree> prefixMap = new ConcurrentSkipListMap<>();

	public LogDefaultBasePrefixTree(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	@Override
	public void log()
	{
		generate();

		logTree(prefixMap);
	}

	void logTree(final Map<BigInteger, Tree> prefixMap)
	{
		prefixMap
		.values()
		.stream()
		.parallel()
		.forEach(treeNode ->
					{
						final var outputStrFinal = new StringBuilder();
						logTree(treeNode, outputStrFinal);
					}
				);
	}

	void logTree(final Tree tree, final StringBuilder outputStr)
	{
		outputStr.append(tree.getPrefixPrime().get());

		if (!tree.getSourcePrimes().isEmpty())
		{
			System.out.println(String.format("Prefix[%s] count[%d] source-primes[%s]",
					outputStr.toString(),
					tree.getSourcePrimes().size(),
					tree.getSourcePrimes().stream().map(BigInteger::toString).collect(Collectors.joining(",", "[", "]"))));
		}

		tree.getNext()
			.values()
			.stream()
			.forEach(treeNode ->
						{
							final var outputStrFinal = new StringBuilder(outputStr);
							outputStrFinal.append(",");

							logTree(treeNode, outputStrFinal);
						}
					);
	}

	public void generate()
	{
		System.out.println(String.format("%n"));

		ps.getPrimeRefStream().forEach(
				curPrime ->
				{
					try
					{
						final var origBaseIdxs = curPrime.getPrimeBaseData().getPrimeBaseIdxs().get(0);
						final var curPrimePrefixIdxs = (BitSet)origBaseIdxs.clone();

						// Prefixes don't include the Prime (n-1) item per the definition of "prefix" used.
						if (!curPrimePrefixIdxs.isEmpty())
							curPrimePrefixIdxs.clear(curPrimePrefixIdxs.length()-1);

						final var curPrefixIdxsIt = curPrimePrefixIdxs.stream().iterator();
						var curPrefixMap = prefixMap;
						if (curPrefixIdxsIt.hasNext())
							do {

								final var curBaseIdx = curPrefixIdxsIt.next();
								final var curPrefixPrime = ps.getPrime(curBaseIdx).get();

								if (!curPrefixMap.containsKey(curPrefixPrime))
								{
									addNextPrefixPrime(curPrefixMap, curPrefixPrime);
								}

								final var curTree = curPrefixMap.get(curPrefixPrime);
								if (!curPrefixIdxsIt.hasNext())
								{
									curTree.getSourcePrimes().add(curPrime.getPrime());
								}

								curPrefixMap = curTree.getNext();
							}
							while (curPrefixIdxsIt.hasNext());
					}
					catch(Exception e)
					{
						log.severe(String.format("Can't show bases for: %d exception:", curPrime.getPrime()));
						log.throwing(this.getClass().getName(), "log", e);
						e.printStackTrace();
					}
				});
	}

	void addNextPrefixPrime(Map<BigInteger, Tree> curTree, BigInteger curPrefixPrime)
	{
		final var nextMap = new TreeMap<BigInteger,Tree>();
		final var newTree = new Tree(curPrefixPrime, nextMap);
		curTree.put(curPrefixPrime, newTree);
	}
}

