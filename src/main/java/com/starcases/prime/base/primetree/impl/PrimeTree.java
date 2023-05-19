package com.starcases.prime.base.primetree.impl;

import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.impl.PrimeBaseGenerator;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;

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
public class PrimeTree extends PrimeBaseGenerator
{
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
	public PrimeTree(@NonNull final BaseTypes baseType, @NonNull final CollectionTrackerIntfc collectionTracker)
	{
		super(baseType);
		this.collectionTracker = collectionTracker;
	}

	@Override
	public void genBasesForPrimeRef(final PrimeRefIntfc curPrime)
	{
		final var origBaseBases = curPrime.getPrimeBaseData().getPrimeBases().get(0);
		final ImmutableLongList curPrimePrefixBases = origBaseBases.toList().toImmutable();

		final var curPrefixIt = collectionTracker.iterator();
		curPrimePrefixBases.forEach(basePrime ->
			{
				final var prime = basePrime;
				curPrefixIt.add(prime);
			});

		curPrime.getPrimeBaseData().addPrimeBases(MutableListFactoryImpl.INSTANCE.of(curPrefixIt.toCollection()), BaseTypes.PRIME_TREE);
	}
}
