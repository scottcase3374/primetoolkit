package com.starcases.prime.base.primetree;

import java.util.ListIterator;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;

/**
 * Represent an iterator for use with tree nodes.  Adds some alternative
 * methods which provide the features needed since
 * the default method signatures did not contain
 * needed items.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface PrimeTreeIteratorIntfc extends ListIterator<PrimeTreeNode>
{
	/**
	 * Override Remove item.
	 * NO-OP
	 */
	@Override
	default void remove()
	{
		// NO-OP
	}

	/**
	 * override nextIndex
	 * NO-OP
	 */
	@Override
	default int nextIndex() {return -1;}

	/**
	 * override previousIndex
	 * NO-OP
	 */
	@Override
	default int previousIndex() {return -1;}

	@Override
	/**
	 * NO-OP
	 */
	default PrimeTreeNode next() { return null; }

	/**
	 * Override previous.
	 * NO-OP
	 */
	@Override
	default PrimeTreeNode previous() { return null; }

	/**
	 * override hasNext
	 * NO-OP
	 */
	@Override
	default boolean hasNext() { return false; }

	@Override
	default boolean hasPrevious() { return false; }

	/**
	 * override set
	 * NO-OP
	 */
	@Override
	default void set(final PrimeTreeNode treeNode)
	{
		// NO-OP
	}

	/**
	 * Overrdie add
	 * NO-OP
	 */
	@Override
	default void add(final PrimeTreeNode treeNode)
	{
		// NO-OP
	}

	/**
	 * Determine if next item is specified prime.
	 * @param prime
	 * @return
	 */
	boolean hasNext(long prime);

	/**
	 * Get next tree node for specified prime
	 * @param prime
	 * @return
	 */
	PrimeTreeNode next(long prime);

	/**
	 * Add next prime as next tree node.
	 * @param prime
	 * @return
	 */
	PrimeTreeNode add(long prime);

	/**
	 * Get a set representation
	 * @return
	 */
	ImmutableLongCollection toCollection();
}
