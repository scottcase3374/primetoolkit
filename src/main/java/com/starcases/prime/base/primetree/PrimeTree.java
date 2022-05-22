package com.starcases.prime.base.primetree;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.LongPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.impl.list.mutable.MultiReaderFastList;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.impl.PData;
import com.starcases.prime.intfc.CollectionTrackerIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;


/**
 * Represents a potentially partial or complete set of primes that sums to a prime
 * in combination with one or more other distinct primes or by itself.
 */
@SuppressWarnings("PMD.LongVariable")
public class PrimeTree extends AbstractPrimeBaseGenerator
{
	private static final Logger LOG = Logger.getLogger(PrimeTree.class.getName());

	final Map<BigInteger, PrimeTreeNode> prefixMap = new ConcurrentHashMap<>();
	private final CollectionTrackerIntfc collectionTracker;

	public PrimeTree(@NonNull final PrimeSourceIntfc ps, @NonNull final CollectionTrackerIntfc collectionTracker)
	{
		super(ps);
		this.collectionTracker = collectionTracker;
	}

	public PrimeTreeIteratorIntfc iterator()
	{
		return new PrimeTreeIterator(this, collectionTracker);
	}

	/**
	 * Execute 'func' against the prime tree data and return a PData
	 * if the predicate is matched. Looking for key for
	 * a prime sum that when added to a new prime results in a new prime.
	 *
	 * @param biFunc
	 * @return
	 */
	public Optional<PData> select(final LongPredicate pred)
	{
		return collectionTracker.select(pred);
	}

	/**
	 * top-level function; iterate over entire dataset to reduce every Prime
	 * @param maxReduce
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	protected void genBasesImpl()
	{
		PrimeToolKit.output(String.format("%n"));
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("PrimeTree genBases()");
		}

		primeSrc.
		getPrimeRefStream(2L, this.preferParallel)
		.forEach(
			curPrime ->
			{
				if (this.baseGenerationOutput && LOG.isLoggable(Level.INFO))
				{
					LOG.info("genBases() - prime " + curPrime.getPrime());
				}

				final var origBaseBases = curPrime.getPrimeBaseData().getPrimeBases().get(0);
				List<BigInteger> curPrimePrefixBases = MultiReaderFastList.newList(origBaseBases);

				final var curPrefixIt = this.iterator();
				curPrimePrefixBases.forEach(basePrime ->
					{
						final var prime = basePrime;
						if (this.isBaseGenerationOutput() && LOG.isLoggable(Level.INFO))
						{
							LOG.info(String.format("handling prime[%d] base-index [%d] base-prime [%d]", curPrime.getPrime(), basePrime, prime));
						}

						curPrefixIt.add(prime);
					});

			curPrime.getPrimeBaseData().addPrimeBases(List.of(curPrefixIt.toSet()), BaseTypes.PRIME_TREE);
		});
	}
}
