package com.starcases.prime.base.primetree.impl;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;

import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.impl.AbstractPrimeBaseGenerator;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.metrics.MetricMonitor;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.LongTaskTimer.Sample;
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
public class PrimeTree extends AbstractPrimeBaseGenerator
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(PrimeTree.class.getName());

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
	 * top-level function; iterate over entire dataset to reduce every Prime
	 * @param maxReduce
	 */
	@Override
	protected void genBasesImpl()
	{
		PrimeToolKit.output(String.format("%n"));
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("PrimeTree genBases()");
		}

		final Optional<LongTaskTimer.Sample> timer = MetricMonitor.longTimer(BaseTypes.PRIME_TREE);

		primeSrc
			.getPrimeRefStream(2L, this.preferParallel)
			.forEach(
				curPrime ->
				{
					if (this.baseGenerationOutput && LOG.isLoggable(Level.FINE))
					{
						LOG.fine("genBases() - prime " + curPrime.getPrime());
					}

					final var origBaseBases = curPrime.getPrimeBaseData().getPrimeBases().get(0);
					final ImmutableLongList curPrimePrefixBases = origBaseBases.toList().toImmutable();

					final var curPrefixIt = collectionTracker.iterator();
					curPrimePrefixBases.forEach(basePrime ->
						{
							final var prime = basePrime;
							if (this.isBaseGenerationOutput() && LOG.isLoggable(Level.FINE))
							{
								LOG.fine(String.format("handling prime[%d] base-index [%d] base-prime [%d]", curPrime.getPrime(), basePrime, prime));
							}

							curPrefixIt.add(prime);
						});

					curPrime.getPrimeBaseData().addPrimeBases(MutableListFactoryImpl.INSTANCE.of(curPrefixIt.toCollection()), BaseTypes.PRIME_TREE);
				});
		timer.ifPresent(Sample::stop);
	}
}
