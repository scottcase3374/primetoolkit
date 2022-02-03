package com.starcases.prime.base.def;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;

class Tree
{
	@Getter
	@Setter
	BigInteger prefixPrime;

	@Getter
	@Setter
	Map<BigInteger, Tree> next;

	@Getter
	@Setter
	List<BigInteger> sourcePrimes = new ArrayList<>();

	Tree(BigInteger prefixPrime)
	{
		this.prefixPrime = prefixPrime;
		this.next = null;
	}
}

@Log
public class LogDefaultBasePrefixTree extends AbstractLogBase
{
	Map<BigInteger, Tree> prefixMap = new TreeMap<>();

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

	void logTree(Map<BigInteger, Tree> prefixMap)
	{
		prefixMap
		.values()
		.stream()
		.forEach(treeNode ->
					{
						final var outputStrFinal = new StringBuilder();
						logTree(treeNode, outputStrFinal);
					}
				);
	}

	void logTree(Tree tree, StringBuilder outputStr)
	{
		outputStr.append(tree.prefixPrime);

		if (!tree.sourcePrimes.isEmpty())
		{
			System.out.println(String.format("Prefix[%s] count[%d] source-primes[%s]", outputStr.toString(), tree.sourcePrimes.size(), tree.sourcePrimes.stream().map(BigInteger::toString).collect(Collectors.joining(",", "[", "]"))));
		}

		tree.next
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

		var curPrimeIt = ps.getPrimeRefIter();
		while (curPrimeIt.hasNext())
		{
			var curPrime = curPrimeIt.next();
			try
			{
				var origBaseIdxs = curPrime.getPrimeBaseData().getPrimeBaseIdxs().get(0);
				var curPrimePrefixIdxs = (BitSet)origBaseIdxs.clone();

				// Prefixes don't include the Prime (n-1) item per the definition of "prefix" used.
				if (!curPrimePrefixIdxs.isEmpty())
					curPrimePrefixIdxs.clear(curPrimePrefixIdxs.length()-1);

				var curPrefixIdxsIt = curPrimePrefixIdxs.stream().iterator();
				var curPrefixMap = prefixMap;
				if (curPrefixIdxsIt.hasNext())
					do {

						var curBaseIdx = curPrefixIdxsIt.next();
						var curPrefixPrime = ps.getPrime(curBaseIdx).get();

						if (!curPrefixMap.containsKey(curPrefixPrime))
						{
							addNextPrefixPrime(curPrefixMap, curPrefixPrime);
						}

						var curTree = curPrefixMap.get(curPrefixPrime);
						if (!curPrefixIdxsIt.hasNext())
						{
							curTree.sourcePrimes.add(curPrime.getPrime());
						}

						curPrefixMap = curTree.next;
					}
					while (curPrefixIdxsIt.hasNext());
			}
			catch(Exception e)
			{
				log.severe(String.format("Can't show bases for: %d exception:", curPrime.getPrime()));
				log.throwing(this.getClass().getName(), "log", e);
				e.printStackTrace();
			}
		}
	}

	void addNextPrefixPrime(Map<BigInteger, Tree> curTree, BigInteger curPrefixPrime)
	{
		var newTree = new Tree(curPrefixPrime);
		var nextMap = new TreeMap<BigInteger,Tree>();
		newTree.next = nextMap;
		curTree.put(curPrefixPrime, newTree);
	}
}

