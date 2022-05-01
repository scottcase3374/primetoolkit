package com.starcases.prime.base.primetree;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

class PrimeTreeNode
{
	private final AtomicReference<BigInteger> prefixPrime = new AtomicReference<>(null);

	private final AtomicReference<Map<BigInteger, PrimeTreeNode>> next = new AtomicReference<>(null);

	// Represents a partial set of primes (e.g. prefix, suffix set) which lead up to the current node.
	private final AtomicReference<Set<BigInteger>> sourcePrimes = new AtomicReference<>(null);
	//final AtomicInteger sourcePrimeCollId = new AtomicInteger();

	public PrimeTreeNode(BigInteger curPrime, Map<BigInteger, PrimeTreeNode> prefix)
	{
		this.prefixPrime.set(curPrime);
		this.next.set(prefix);
	}

	public Set<BigInteger> getSourcePrimes()
	{
		return sourcePrimes.getAcquire();
	}

	public Set<BigInteger> setSourcePrimes(UnaryOperator<Set<BigInteger>> supplier)
	{
		return sourcePrimes.updateAndGet(supplier);
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