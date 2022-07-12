package com.starcases.prime.base.primetree;

import java.util.Optional;
import java.util.function.LongPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.MutableLongObjectMapFactoryImpl;

import com.codahale.metrics.Timer;
import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.impl.PData;
import com.starcases.prime.intfc.CollectionTrackerIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.metrics.MetricMonitor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;


/**
 * Represents a potentially partial or complete set of primes that sums to a prime
 * in combination with one or more other distinct primes or by itself.
 *
 * Used both internally in the initial base/prime creation as part of an efficiency
 * improvement and also as another base representation by itself.
 *
 */
@SuppressWarnings("PMD.LongVariable")
public class PrimeTree extends AbstractPrimeBaseGenerator
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(PrimeTree.class.getName());

	/**
	 * prefix mape
	 */
	@Getter(AccessLevel.PACKAGE)
	private final MutableLongObjectMap<PrimeTreeNode> prefixMap =  MutableLongObjectMapFactoryImpl.INSTANCE.empty();

	/**
	 * container for tracking the unique set of prefixes/trees
	 */
	@Getter(AccessLevel.PRIVATE)
	private final CollectionTrackerIntfc collectionTracker;

	/**
	 * Constructor for the prime tree
	 * @param primeSrc
	 * @param collectionTracker
	 */
	public PrimeTree(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final CollectionTrackerIntfc collectionTracker)
	{
		super(primeSrc);
		this.collectionTracker = collectionTracker;
	}

	/**
	 * get iterator to the tree info
	 * @return
	 */
	public PrimeTreeIteratorIntfc iterator()
	{
		return new PrimeTreeIterator(this, collectionTracker);
	}

	/**
	 * Execute 'func' against the prime tree data and return a PData
	 * if the predicate is matched. Looking for key for
	 * a prime sum that when added to a new prime results in a new prime.
	 *
	 * NOTE: Desire lowest matching value found
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

		MetricMonitor.addTimer(BaseTypes.PRIME_TREE,"Gen PrimeTree");
		primeSrc
			.getPrimeRefStream(2L, this.preferParallel)
			.forEach(
				curPrime ->
				{
					if (this.baseGenerationOutput && LOG.isLoggable(Level.INFO))
					{
						LOG.info("genBases() - prime " + curPrime.getPrime());
					}

					try (Timer.Context context = MetricMonitor.time(BaseTypes.PRIME_TREE).orElse(null))
					{
						final var origBaseBases = curPrime.getPrimeBaseData().getPrimeBases().get(0);
						final ImmutableLongList curPrimePrefixBases = origBaseBases.toList().toImmutable();

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

						curPrime.getPrimeBaseData().addPrimeBases(MutableListFactoryImpl.INSTANCE.of(curPrefixIt.toCollection()), BaseTypes.PRIME_TREE);
					}
				});
	}
}
