package com.starcases.prime.base.prefixtree;

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
public class PrefixIterator implements PrefixIteratorIntfc
{
	private BasePrefixTree tree;
	private Map<BigInteger, PrefixTreeNode> treeNodeMap = null;
	private List<PrefixTreeNode> sourceTreeNodes = null;

	PrefixIterator(BasePrefixTree bpt, boolean trackSourceTreeNodes)
	{
		tree = bpt;
		treeNodeMap = tree.prefixMap;
		if (trackSourceTreeNodes)
		{
			 sourceTreeNodes =  FastList.newList();
		}
	}

	/**
	 * Only returns items used by current iterator using add().
	 */
	@Override
	public Set<BigInteger> toSet()
	{
		return sourceTreeNodes.stream().map(tn -> tn.getPrefixPrime().get()).collect(Collectors.toCollection(TreeSortedSet::newSet));
	}

	/**
	 * This only walks the tree; no tracking of source nodes is performed.
	 */
	@Override
	public PrefixTreeNode next(BigInteger i)
	{
		final var tn = treeNodeMap.get(i);
		treeNodeMap = tn.getNext();
		return tn;
	}

	/**
	 *
	 * Side effect: tracks source nodes enabling toSet() to return the items representing a prefix.
	 */
	@Override
	public PrefixTreeNode add(BigInteger i)
	{
		final var tn = treeNodeMap.computeIfAbsent(i, f ->
												{
													final var nextMap = new ConcurrentHashMap<BigInteger,PrefixTreeNode>();

													return  new PrefixTreeNode(i, nextMap);
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
