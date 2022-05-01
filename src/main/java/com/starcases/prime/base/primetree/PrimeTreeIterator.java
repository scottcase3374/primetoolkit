package com.starcases.prime.base.primetree;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.impl.list.mutable.FastList;
import  org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;

/**
 * Instances are intended for use in a single thread and NOT
 * shared across threads.
 */
public class PrimeTreeIterator implements PrimeTreeIteratorIntfc
{
	final List<PrimeTreeNode> sourceTreeNodes =  FastList.newList();
	final PrimeTree tree;
	Map<BigInteger, PrimeTreeNode> treeNodeMap;

	PrimeTreeIterator(PrimeTree bpt)
	{
		tree = bpt;
		treeNodeMap = tree.prefixMap;
	}

	/**
	 *
	 */
	@Override
	public Set<BigInteger> toSet()
	{
		 final var ptn = sourceTreeNodes.get(sourceTreeNodes.size()-1);
		 var baseSet  = ptn.getSourcePrimes();

		if (baseSet == null)
		{
			final var sp1 = sourceTreeNodes.stream()
					.map(PrimeTreeNode::getPrefixPrime).collect(Collectors.toCollection(TreeSortedSet::newSet));

			ptn.setSourcePrimes(s1 -> s1 != null ? s1 : sp1);
			baseSet = sp1;
		}

		return baseSet;
	}

	/**
	 * This walks the tree and tracks the navigated source nodes.
	 */
	@Override
	public PrimeTreeNode next(BigInteger i)
	{
		final var tn = treeNodeMap.get(i);
		sourceTreeNodes.add(tn);
		treeNodeMap = tn.getNext();
		return tn;
	}

	/**
	 *
	 * Side effect: tracks source nodes enabling toSet() to return the items representing a prefix.
	 */
	@Override
	public PrimeTreeNode add(BigInteger i)
	{
		final var tn = treeNodeMap.computeIfAbsent(i, f ->
												{
													final var nextMap = new ConcurrentHashMap<BigInteger,PrimeTreeNode>();

													return  new PrimeTreeNode(i, nextMap);
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
