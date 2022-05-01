package com.starcases.prime.base.primetree;

import java.math.BigInteger;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

public interface PrimeTreeIteratorIntfc extends ListIterator<PrimeTreeNode>
{
	default void remove() {}
	default int nextIndex() {return -1;}
	default int previousIndex() {return -1;}
	default PrimeTreeNode next() throws NoSuchElementException { return null; }
	default PrimeTreeNode previous() { return null; }
	default boolean hasNext() { return false; }
	default boolean hasPrevious() { return false; }
	default void set(PrimeTreeNode tn) {}
	default void add(PrimeTreeNode tn) {}

	boolean hasNext(BigInteger i);
	PrimeTreeNode next(BigInteger i);
	PrimeTreeNode add(BigInteger i);

	Set<BigInteger> toSet();
}
