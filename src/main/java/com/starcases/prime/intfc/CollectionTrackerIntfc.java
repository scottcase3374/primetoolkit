package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;
import java.util.function.LongPredicate;

import com.starcases.prime.impl.PData;

/**
 * Interface for use with managing collections and handing out consistent
 * references to the same collection instance when target members
 * are the same instead of having multiple copies of large collections
 * in memory.
 */
public interface CollectionTrackerIntfc
{
	PData track(Set<BigInteger> collection);
	Optional<PData> get(long key);
	Optional<PData> select(LongPredicate pred);

	void log();
}