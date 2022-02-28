package com.starcases.prime.base.prefixtree;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public class BasePrefixTree extends AbstractPrimeBaseGenerator
{
	final Map<BigInteger, PrefixTreeNode> prefixMap = new ConcurrentSkipListMap<>();

	@NonNull
	private BaseTypes activeBaseId = BaseTypes.PREFIX_TREE;

	public BasePrefixTree(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	/**
	 * top-level function; iterate over entire dataset to reduce every Prime
	 * @param maxReduce
	 */
	public void genBases()
	{
		System.out.println(String.format("%n"));

		ps.getPrimeRefStream(preferParallel).forEach(
				curPrime ->
				{
					try
					{
						final var origBaseIdxs = curPrime.getPrimeBaseData().getPrimeBaseIdxs().get(0);
						final var curPrimePrefixIdxs = new ConcurrentLinkedQueue<Integer>(origBaseIdxs);

						// Prefixes don't include the Prime (n-1) item per the definition of "prefix" used.
						if (!curPrimePrefixIdxs.isEmpty())
							curPrimePrefixIdxs.remove(curPrimePrefixIdxs.size()-1);

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

	void addNextPrefixPrime(Map<BigInteger, PrefixTreeNode> curTree, BigInteger curPrefixPrime)
	{
		final var nextMap = new TreeMap<BigInteger,PrefixTreeNode>();
		final var newTree = new PrefixTreeNode(curPrefixPrime, nextMap);
		curTree.put(curPrefixPrime, newTree);
	}
}
