package com.starcases.prime.base.prefixtree;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Getter;

class PrefixTreeNode {
	@Getter
	public AtomicReference<BigInteger> prefixPrime = new AtomicReference<>();

	@Getter
	public Map<BigInteger, PrefixTreeNode> next = new ConcurrentHashMap<>();

	@Getter
	public List<BigInteger> sourcePrimes;

	public PrefixTreeNode(BigInteger curPrime, Map<BigInteger, PrefixTreeNode> prefix)
	{
		this.prefixPrime.set(curPrime);
		this.next = prefix;
	}
}