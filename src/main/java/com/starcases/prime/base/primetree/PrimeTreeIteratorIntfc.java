package com.starcases.prime.base.primetree;

import java.math.BigInteger;
import java.util.ListIterator;
import java.util.Set;

/**
 * Represent an iterator for use with tree nodes.  Adds some alternative
 * methods which provide the features needed since
 * the default method signatures did not contain
 * needed items.
 */
public interface PrimeTreeIteratorIntfc extends ListIterator<PrimeTreeNode>
{
	@Override
	default void remove() {}
	@Override
	default int nextIndex() {return -1;}

	@Override
	default int previousIndex() {return -1;}

	@Override
	default PrimeTreeNode next() { return null; }

	@Override
	default PrimeTreeNode previous() { return null; }

	@Override
	default boolean hasNext() { return false; }

	@Override
	default boolean hasPrevious() { return false; }

	@Override
	default void set(final PrimeTreeNode tn) {}

	@Override
	default void add(final PrimeTreeNode tn) {}

	boolean hasNext(BigInteger i);
	PrimeTreeNode next(BigInteger i);
	PrimeTreeNode add(BigInteger i);

	Set<BigInteger> toSet();
}
