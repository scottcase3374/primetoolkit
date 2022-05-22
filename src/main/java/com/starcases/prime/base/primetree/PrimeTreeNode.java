package com.starcases.prime.base.primetree;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


import com.starcases.prime.intfc.CollectionTrackerIntfc;

/**
 * Top-level class for representing a prime as a sum
 * of unique smaller primes. A tree/graph style
 * representation is used internally.
 */
class PrimeTreeNode
{
	private final AtomicReference<BigInteger> prefixPrime = new AtomicReference<>(null);

	private final AtomicReference<Map<BigInteger, PrimeTreeNode>> next = new AtomicReference<>(null);

	private final CollectionTrackerIntfc collTrack;

	public PrimeTreeNode(final BigInteger curPrime, final Map<BigInteger, PrimeTreeNode> prefix, final CollectionTrackerIntfc collTrack)
	{
		this.prefixPrime.set(curPrime);
		this.next.set(prefix);
		this.collTrack = collTrack;
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	public Optional<Set<BigInteger>> getSourcePrimes(final long key)
	{
		return collTrack.get(key).map(c -> c.coll());
	}

	public Set<BigInteger> setSourcePrimes(final Set<BigInteger> supplier)
	{
		return collTrack.track(supplier).coll();
	}

	public BigInteger getPrefixPrime()
	{
		return prefixPrime.getAcquire();
	}

	public Map<BigInteger, PrimeTreeNode> getNext()
	{
		return next.getAcquire();
	}
}