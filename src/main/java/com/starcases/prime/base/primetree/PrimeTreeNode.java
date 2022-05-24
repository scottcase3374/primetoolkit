package com.starcases.prime.base.primetree;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.starcases.prime.impl.PData;
import com.starcases.prime.intfc.CollectionTrackerIntfc;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Top-level class for representing a prime as a sum
 * of unique smaller primes. A tree/graph style
 * representation is used internally.
 */
class PrimeTreeNode
{
	/**
	 *
	 */
	private final AtomicReference<BigInteger> prefixPrime = new AtomicReference<>(null);

	/**
	 * next link in tree
	 */
	private final AtomicReference<Map<BigInteger, PrimeTreeNode>> next = new AtomicReference<>(null);

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
	public PrimeTreeNode(final BigInteger curPrime, final Map<BigInteger, PrimeTreeNode> prefix, final CollectionTrackerIntfc collTrack)
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
	@SuppressWarnings("PMD.LawOfDemeter")
	public Optional<Set<BigInteger>> getSourcePrimes(final long key)
	{
		return collTrack.get(key).map(PData::coll);
	}

	/**
	 * Track the set of primes provided by the supplier
	 * @param supplier
	 * @return
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	public Set<BigInteger> assignSourcePrimes(final Set<BigInteger> supplier)
	{
		return collTrack.track(supplier).coll();
	}

	/**
	 * Get the associated prefix prime
	 * @return
	 */
	public BigInteger getPrefixPrime()
	{
		return prefixPrime.getAcquire();
	}

	/**
	 * get next link
	 * @return
	 */
	public Map<BigInteger, PrimeTreeNode> getNext()
	{
		return next.getAcquire();
	}
}