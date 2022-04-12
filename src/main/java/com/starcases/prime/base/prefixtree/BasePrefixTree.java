package com.starcases.prime.base.prefixtree;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.collections.impl.list.mutable.MultiReaderFastList;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;


public class BasePrefixTree extends AbstractPrimeBaseGenerator
{
	private static final Logger log = Logger.getLogger(BasePrefixTree.class.getName());

	final Map<BigInteger, PrefixTreeNode> prefixMap = new ConcurrentHashMap<>();

	public BasePrefixTree(@NonNull PrimeSourceIntfc ps)
	{
		super(ps);
	}

	public PrefixIteratorIntfc iterator()
	{
		return new PrefixIterator(this, true);
	}

	/**
	 * top-level function; iterate over entire dataset to reduce every Prime
	 * @param maxReduce
	 */
	@Override
	public void genBases(boolean trackGenTime)
	{
		System.out.println(String.format("%n"));
		log.info("BasePrefixTree genBases()");

		final var prStream = ps.getPrimeRefStream(2L, false);
		if (trackGenTime)
			event(true);

		prStream.forEach(
				curPrime ->
				{
					if (this.doLog)
						log.info("genBases() - prime " + curPrime.getPrime());

					final var origBaseBases = curPrime.getPrimeBaseData().getPrimeBases().get(0);
					final List<BigInteger> curPrimePrefixBases = MultiReaderFastList.newList(origBaseBases);

					// Prefixes don't include the Prime (n-1) item per the definition of "prefix" used.
					if (!curPrimePrefixBases.isEmpty())
						curPrimePrefixBases.remove(curPrimePrefixBases.size()-1);

					var curPrefixIt = this.iterator();
					PrefixTreeNode [] tn = {null};
					curPrimePrefixBases.forEach(basePrime ->
						{
							final var prime = basePrime;
							if (this.doLog)
								log.info(String.format("handling prime[%d] base-index [%d] base-prime [%d]", curPrime.getPrime(), basePrime, prime));

							tn[0] = curPrefixIt.add(prime);
						});


					tn[0].setSourcePrimes(curPrefixIt.toSet());
					curPrime.getPrimeBaseData().addPrimeBases(tn[0].getSourcePrimes(), BaseTypes.PREFIX_TREE);
				});

		if (trackGenTime)
			event(false);
	}
}
