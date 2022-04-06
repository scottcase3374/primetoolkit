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

					final var origBaseBases = curPrime.getPrimeBaseData().getPrimeBases().get(0);
					final var curPrimePrefixBases = new ConcurrentLinkedDeque<BigInteger>(origBaseBases);

					// Prefixes don't include the Prime (n-1) item per the definition of "prefix" used.
					if (!curPrimePrefixBases.isEmpty())
						curPrimePrefixBases.removeLast();

					final var curPrefixBasesIt = curPrimePrefixBases.stream().iterator();
					var curPrefixIt = this.iterator();
					PrefixTreeNode [] tn = {null};
					curPrefixBasesIt.forEachRemaining(basePrime ->
						{
							final var prime = basePrime;
							if (this.doLog)
								log.info(String.format("handling prime[%d] base-index [%d] base-prime [%d]", curPrime.getPrime(), basePrime, prime));

							tn[0] = curPrefixIt.add(prime);
						});


					tn[0].setSourcePrimes(curPrefixIt.toSet());
					curPrime.getPrimeBaseData().addPrimeBases(tn[0].getSourcePrimes(), BaseTypes.PREFIX_TREE);
				});
	}
}
