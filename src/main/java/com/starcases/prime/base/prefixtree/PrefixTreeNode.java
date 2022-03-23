package com.starcases.prime.base.prefixtree;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Getter;
import lombok.Setter;

class PrefixTreeNode
{
	@Getter
	private AtomicReference<BigInteger> prefixPrime = new AtomicReference<>();

	@Getter
	private Map<BigInteger, PrefixTreeNode> next = new ConcurrentHashMap<>();

	@Getter
	@Setter
	private Set<BigInteger> sourcePrimes;

	public PrefixTreeNode(BigInteger curPrime, Map<BigInteger, PrefixTreeNode> prefix)
	{
		this.prefixPrime.set(curPrime);
		this.next = prefix;
	}
}