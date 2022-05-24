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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Instances are intended for use in a single thread and NOT
 * shared across threads.
 */
public class PrimeTreeIterator implements PrimeTreeIteratorIntfc
{
	/**
	 * Source nodes tracked
	 */
	@Getter(AccessLevel.PRIVATE)
	private final List<PrimeTreeNode> sourceTreeNodes =  FastList.newList();

	/**
	 * Ref to the a tree iterating over
	 */
	@Getter(AccessLevel.PRIVATE)
	private final PrimeTree tree;

	/**
	 * container to track unique instances of the unique prefix/tree info
	 */
	@Getter(AccessLevel.PRIVATE)
	private final CollectionTrackerIntfc collTrack;

	/**
	 * Ref to next tree node map
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private Map<BigInteger, PrimeTreeNode> treeNodeMap;

	/**
	 * Current sum of nodes in iterated path
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private BigInteger curSum = BigInteger.ZERO;


	/**
	 * constructor for the iterator
	 * @param bpt
	 * @param collTrack
	 */
	PrimeTreeIterator(final PrimeTree bpt, final CollectionTrackerIntfc collTrack)
	{
		tree = bpt;
		treeNodeMap = tree.getPrefixMap();
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

							ptn.assignSourcePrimes(sp1);
							return sp1;
				 		});
	}

	/**
	 * This walks the tree and tracks the navigated source nodes.
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public PrimeTreeNode next(final BigInteger prime)
	{
		final var treeNode = treeNodeMap.get(prime);
		sourceTreeNodes.add(treeNode);
		treeNodeMap = treeNode.getNext();
		return treeNode;
	}

	/**
	 *
	 * Side effect: tracks source nodes enabling toSet() to
	 * return the items representing a prefix.
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public PrimeTreeNode add(final BigInteger prime)
	{
		final var treeNode = treeNodeMap.computeIfAbsent(prime, f ->
												{
													final var nextMap = new ConcurrentHashMap<BigInteger,PrimeTreeNode>();

													return  new PrimeTreeNode(prime, nextMap, collTrack);
												}
											);

		curSum = curSum.add(prime);
		sourceTreeNodes.add(treeNode);
		treeNodeMap = treeNode.getNext();
		return treeNode;
	}

	@Override
	public boolean hasNext(final BigInteger prime)
	{
		return treeNodeMap.containsKey(prime);
	}
}
