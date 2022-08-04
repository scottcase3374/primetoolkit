package com.starcases.prime.base.primetree;

import java.util.Optional;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;

import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.primitive.MutableLongObjectMapFactoryImpl;

import com.starcases.prime.intfc.CollectionTrackerIntfc;

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
	private final MutableList<PrimeTreeNode> sourceTreeNodes =  FastList.newList();

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
	private MutableLongObjectMap<PrimeTreeNode> treeNodeMap;

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
	public PrimeTreeIterator(final PrimeTree bpt, final CollectionTrackerIntfc collTrack)
	{
		tree = bpt;
		treeNodeMap = tree.getPrefixMap();
		this.collTrack = collTrack;
	}

	/**
	 *
	 */
	@Override
	public ImmutableLongCollection toCollection()
	{
		 final PrimeTreeNode ptn = sourceTreeNodes.get(sourceTreeNodes.size()-1);
		 final Optional<ImmutableLongCollection> optSet  = ptn.getSourcePrimes(curSum);

		 return optSet.orElseGet(() ->
				 		{
				 			final ImmutableLongCollection sp1 = this.sourceTreeNodes.collectLong(PrimeTreeNode::getPrefixPrime).toImmutable();

							ptn.assignSourcePrimes(sp1);
							return sp1;
				 		});
	}

	/**
	 * This walks the tree and tracks the navigated source nodes.
	 */
	@Override
	public PrimeTreeNode next(final long prime)
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
	public PrimeTreeNode add(final long prime)
	{
		curSum += prime;
		final PrimeTreeNode treeNode = treeNodeMap.getIfAbsentPut( prime, () ->
												{
													final MutableLongObjectMap<PrimeTreeNode> nextMap = MutableLongObjectMapFactoryImpl.INSTANCE.empty();
													return  new PrimeTreeNode(prime, nextMap, collTrack);
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
