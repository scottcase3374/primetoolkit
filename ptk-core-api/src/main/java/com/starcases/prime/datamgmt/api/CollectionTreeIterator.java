package com.starcases.prime.datamgmt.api;

import java.util.Optional;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;

import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.primitive.MutableLongObjectMapFactoryImpl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Instances are intended for use in a single thread and NOT
 * shared across threads.
 */
public class CollectionTreeIterator implements CollectionTreeIteratorIntfc
{
	/**
	 * Source nodes tracked
	 */
	@Getter(AccessLevel.PRIVATE)
	private final MutableList<CollectionTreeNode> sourceTreeNodes =  FastList.newList();

	/**
	 * Ref to the a collTree iterating over
	 */
	@Getter(AccessLevel.PRIVATE)
	private final CollectionTrackerIntfc collTracker;

	/**
	 * container to track unique instances of the unique prefix/collTree info
	 */
	//@Getter(AccessLevel.PRIVATE)
	//private final CollectionTrackerIntfc collTrack;

	/**
	 * Ref to next collTree node map
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private MutableLongObjectMap<CollectionTreeNode> treeNodeMap;

	/**
	 * Current sum of nodes in iterated path
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private long curSum;

	/**
	 * constructor for the iterator
	 * @param bpt
	 * @param collTrack
	 */
	public CollectionTreeIterator(final CollectionTrackerIntfc bpt)
	{
		collTracker = bpt;
		treeNodeMap = collTracker.getPrefixMap();
	}

	/**
	 *
	 */
	@Override
	public ImmutableLongCollection toCollection()
	{
		 final CollectionTreeNode ptn = sourceTreeNodes.get(sourceTreeNodes.size()-1);
		 final Optional<ImmutableLongCollection> optSet  = ptn.getSourcePrimes(curSum);

		 return optSet.orElseGet(() ->
				 		{
				 			final ImmutableLongCollection sp1 = this.sourceTreeNodes.collectLong(CollectionTreeNode::getPrefixPrime).toImmutable();

							ptn.assignSourcePrimes(sp1);
							return sp1;
				 		});
	}

	/**
	 * This walks the collTree and tracks the navigated source nodes.
	 */
	@Override
	public CollectionTreeNode next(final long prime)
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
	@Override
	public CollectionTreeNode add(final long prime)
	{
		curSum += prime;
		final CollectionTreeNode treeNode = treeNodeMap.getIfAbsentPut( prime, () ->
												{
													final MutableLongObjectMap<CollectionTreeNode> nextMap = MutableLongObjectMapFactoryImpl.INSTANCE.empty();
													return  new CollectionTreeNode(prime, nextMap, collTracker);
												}
											);

		treeNodeMap = treeNode.getNext();
		sourceTreeNodes.add(treeNode);

		return treeNode;
	}

	@Override
	public boolean hasNext(final long prime)
	{
		return treeNodeMap.containsKey(prime);
	}
}
