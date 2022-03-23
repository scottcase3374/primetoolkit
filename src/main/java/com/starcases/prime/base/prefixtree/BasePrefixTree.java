package com.starcases.prime.base.prefixtree;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Logger;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;


public class BasePrefixTree extends AbstractPrimeBaseGenerator
{
	private static final Logger log = Logger.getLogger(BasePrefixTree.class.getName());

	final Map<BigInteger, PrefixTreeNode> prefixMap = new ConcurrentSkipListMap<>();

	public BasePrefixTree(@NonNull PrimeSourceIntfc ps)
	{
		super(ps);
	}

	public PrefixIteratorIntfc iterator()
	{
		return new PrefixIterator(this);
	}

	/**
	 * top-level function; iterate over entire dataset to reduce every Prime
	 * @param maxReduce
	 */
	@Override
	public void genBases()
	{
		System.out.println(String.format("%n"));
		log.info("BasePrefixTree genBases()");

		final var prStream = ps.getPrimeRefStream(false).skip(2);
		prStream.forEach(
				curPrime ->
				{
					if (this.doLog)
						log.info("genBases() - prime " + curPrime.getPrime());

					final var origBaseIdxs = curPrime.getPrimeBaseData().getPrimeBaseIdxs().get(0);
					final var curPrimePrefixIdxs = new ConcurrentLinkedDeque<Integer>(origBaseIdxs);

					// Prefixes don't include the Prime (n-1) item per the definition of "prefix" used.
					if (!curPrimePrefixIdxs.isEmpty())
						curPrimePrefixIdxs.removeLast();

					final var curPrefixIdxsIt = curPrimePrefixIdxs.stream().iterator();
					var curPrefixIt = this.iterator();
					PrefixTreeNode [] tn = {null};
					curPrefixIdxsIt.forEachRemaining(i ->
						{
							final var bi = ps.getPrime(i).get();
							if (this.doLog)
								log.info(String.format("handling prime[%d] base-index [%d] base-prime [%d]", curPrime.getPrime(), i, bi));

							tn[0] = curPrefixIt.add(bi);
						});


					tn[0].setSourcePrimes(curPrefixIt.toSet());
					curPrime.getPrimeBaseData().addPrimeBases(tn[0].getSourcePrimes(), BaseTypes.PREFIX_TREE);
				});
	}
}
