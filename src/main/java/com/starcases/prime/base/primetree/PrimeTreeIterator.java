package com.starcases.prime.base.primetree;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.impl.list.mutable.FastList;
import  org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.CollectionTrackerIntfc;

import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;

/**
 * Instances are intended for use in a single thread and NOT
 * shared across threads.
 */
public class PrimeTreeIterator implements PrimeTreeIteratorIntfc
{
	final List<PrimeTreeNode> sourceTreeNodes =  FastList.newList();
	final PrimeTree tree;
	final CollectionTrackerIntfc collTrack;

	private Map<BigInteger, PrimeTreeNode> treeNodeMap;
	private BigInteger curSum = BigInteger.ZERO;


	PrimeTreeIterator(final PrimeTree bpt, final CollectionTrackerIntfc collTrack)
	{
		tree = bpt;
		treeNodeMap = tree.prefixMap;
		this.collTrack = collTrack;
	}

	/**
	 *
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public Set<BigInteger> toSet()
	{
		 final var ptn = sourceTreeNodes.get(sourceTreeNodes.size()-1);
		 final var optSet  = ptn.getSourcePrimes(curSum.longValue());


		 return optSet.orElseGet(() ->
				 		{
				 			final var sp1 = sourceTreeNodes.stream()
									.map(PrimeTreeNode::getPrefixPrime)
									.collect(Collectors.toCollection(TreeSortedSet::newSet));

							ptn.setSourcePrimes(sp1);
							return sp1;
				 		});
	}

	/**
	 * This walks the tree and tracks the navigated source nodes.
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public PrimeTreeNode next(final BigInteger i)
	{
		final var tn = treeNodeMap.get(i);
		sourceTreeNodes.add(tn);
		treeNodeMap = tn.getNext();
		return tn;
	}

	/**
	 *
	 * Side effect: tracks source nodes enabling toSet() to
	 * return the items representing a prefix.
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public PrimeTreeNode add(final BigInteger i)
	{
		final var tn = treeNodeMap.computeIfAbsent(i, f ->
												{
													final var nextMap = new ConcurrentHashMap<BigInteger,PrimeTreeNode>();

													return  new PrimeTreeNode(i, nextMap, collTrack);
												}
											);

		curSum = curSum.add(i);
		sourceTreeNodes.add(tn);
		treeNodeMap = tn.getNext();
		return tn;
	}

	@Override
	public boolean hasNext(final BigInteger i)
	{
		return treeNodeMap.containsKey(i);
	}
}
