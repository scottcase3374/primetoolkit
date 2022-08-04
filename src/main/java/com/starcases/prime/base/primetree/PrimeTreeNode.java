package com.starcases.prime.base.primetree;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.starcases.prime.impl.PData;
import com.starcases.prime.intfc.CollectionTrackerIntfc;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Top-level class for representing a prime as a sum
 * of unique smaller primes. A tree/graph style
 * representation is used internally.
 */
public class PrimeTreeNode
{
	/**
	 *
	 */
	private final AtomicLong prefixPrime = new AtomicLong();

	/**
	 * next link in tree
	 */
	private final AtomicReference<MutableLongObjectMap<PrimeTreeNode>> next = new AtomicReference<>(null);

	/**
	 * collection tracking
	 */
	@Getter(AccessLevel.PRIVATE)
	private final CollectionTrackerIntfc collTrack;

	/**
	 * constructor for the prefix tree / collection tracking
	 * @param curPrime
	 * @param prefix
	 * @param collTrack
	 */
	public PrimeTreeNode(final long curPrime, final MutableLongObjectMap<PrimeTreeNode> prefix, final CollectionTrackerIntfc collTrack)
	{
		this.prefixPrime.set(curPrime);
		this.next.set(prefix);
		this.collTrack = collTrack;
	}

	/**
	 * Get the set of primes associated with the key
	 * @param key
	 * @return
	 */
	public Optional<ImmutableLongCollection> getSourcePrimes(final long key)
	{
		return collTrack.get(key).map(PData::toCanonicalCollection);
	}

	/**
	 * Track the set of primes provided by the supplier
	 * @param sp1
	 * @return
	 */
	public ImmutableLongCollection assignSourcePrimes(final ImmutableLongCollection sp1)
	{
		return collTrack.track(sp1).toCanonicalCollection();
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
	public MutableLongObjectMap<PrimeTreeNode> getNext()
	{
		return next.getAcquire();
	}
}