package com.starcases.prime.base.prefixtree;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class PrefixIterator implements PrefixIteratorIntfc
{
	private BasePrefixTree tree;
	private Map<BigInteger, PrefixTreeNode> treeNodeMap = null;
	private Set<PrefixTreeNode> sourceTreeNodes = new HashSet<>();

	PrefixIterator(BasePrefixTree bpt)
	{
		tree = bpt;
		treeNodeMap = tree.prefixMap;
	}

	@Override
	public Set<BigInteger> toSet()
	{
		return sourceTreeNodes.stream().map(tn -> tn.getPrefixPrime().get()).collect(Collectors.toCollection(TreeSet<BigInteger>::new));
	}

	@Override
	public PrefixTreeNode next(BigInteger i)
	{
		final var tn = treeNodeMap.get(i);
		sourceTreeNodes.add(tn);
		treeNodeMap = tn.getNext();
		return tn;
	}

	@Override
	public PrefixTreeNode add(BigInteger i)
	{
		final var tn = treeNodeMap.computeIfAbsent(i, f ->
												{
													final var nextMap = new ConcurrentSkipListMap<BigInteger,PrefixTreeNode>();
													final var newTree = new PrefixTreeNode(i, nextMap);
													return newTree;
												}
											);

		sourceTreeNodes.add(tn);
		treeNodeMap = tn.getNext();
		return tn;
	}

	@Override
	public boolean hasNext(BigInteger i)
	{
		return treeNodeMap.containsKey(i);
	}
}
