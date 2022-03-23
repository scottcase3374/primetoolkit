package com.starcases.prime.base.prefixtree;

import java.math.BigInteger;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

public interface PrefixIteratorIntfc extends ListIterator<PrefixTreeNode>
{
	default void remove() {}
	default int nextIndex() {return -1;}
	default int previousIndex() {return -1;}
	default PrefixTreeNode next() throws NoSuchElementException { return null; }
	default PrefixTreeNode previous() { return null; }
	default boolean hasNext() { return false; }
	default boolean hasPrevious() { return false; }
	default void set(PrefixTreeNode tn) {}
	default void add(PrefixTreeNode tn) {}

	boolean hasNext(BigInteger i);
	PrefixTreeNode next(BigInteger i);
	PrefixTreeNode add(BigInteger i);

	Set<BigInteger> toSet();
}
