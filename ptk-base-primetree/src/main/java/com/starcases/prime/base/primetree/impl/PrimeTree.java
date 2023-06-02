package com.starcases.prime.base.primetree.impl;

import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;

import com.starcases.prime.base.api.BaseGenFactoryIntfc;
import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.impl.AbsPrimeBaseGen;
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
class PrimeTree extends AbsPrimeBaseGen
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
	public PrimeTree(@NonNull final BaseTypesIntfc baseType, @NonNull final CollectionTrackerIntfc collectionTracker)
	{
		super(baseType);
		this.collectionTracker = collectionTracker;
	}

	/**
	 * Generate bases for defined base types for the specified prime.
	 */
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

		curPrime.getPrimeBaseData().addPrimeBases(MutableListFactoryImpl.INSTANCE.of(curPrefixIt.toCollection()), PrimeTreeBaseType.PRIME_TREE);
	}

	/**
	 * fluent style method for setting flag for whether base construction can use multiple CPU cores.
	 * @param preferParallel Ignored for PrimeTree - must always be false
	 *
	 * @return
	 */
	@Override
	public BaseGenFactoryIntfc doPreferParallel(final boolean preferParallel)
	{
		this.preferParallel = false;
		return this;
	}
}
