package com.starcases.prime.datamgmt.api;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Top-level class for representing a prime as a sum
 * of unique smaller primes. A tree/graph style
 * representation is used internally.
 */
public class CollectionTreeNode
{
	/**
	 *
	 */
	private final AtomicLong prefixPrime = new AtomicLong();

	/**
	 * next link in tree
	 */
	private final AtomicReference<MutableLongObjectMap<CollectionTreeNode>> next = new AtomicReference<>(null);

	/**
	 * collection tracking
	 */
	@Getter(AccessLevel.PRIVATE)
	private final CollectionTrackerIntfc collTracker;

	/**
	 * constructor for the prefix tree / collection tracking
	 * @param curPrime
	 * @param prefix
	 * @param collTrack
	 */
	public CollectionTreeNode(final long curPrime, final MutableLongObjectMap<CollectionTreeNode> prefix, final CollectionTrackerIntfc collTracker)
	{
		this.prefixPrime.set(curPrime);
		this.next.set(prefix);
		this.collTracker = collTracker;
	}

	/**
	 * Get the set of primes associated with the key
	 * @param key
	 * @return
	 */
	public Optional<ImmutableLongCollection> getSourcePrimes(final long key)
	{
		return collTracker.get(key).map(PData::toCanonicalCollection);
	}

	/**
	 * Track the set of primes provided by the supplier
	 * @param sp1
	 * @return
	 */
	public ImmutableLongCollection assignSourcePrimes(final ImmutableLongCollection sp1)
	{
		return collTracker.track(sp1).toCanonicalCollection();
	}

	/**
	 * Get the associated prefix prime
	 * @return
	 */
	public long getPrefixPrime()
	{
		return prefixPrime.getAcquire();
	}

	/**
	 * get next link
	 * @return
	 */
	public MutableLongObjectMap<CollectionTreeNode> getNext()
	{
		return next.getAcquire();
	}
}