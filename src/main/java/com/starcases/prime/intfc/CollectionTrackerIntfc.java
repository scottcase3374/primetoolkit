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
@SuppressWarnings("PMD.CommentSize")
public interface CollectionTrackerIntfc
{
	/**
	 * Specify set of Big integers to track as group.
	 * @param collection
	 * @return
	 */
	PData track(Set<BigInteger> collection);

	/**
	 * Get tracking information associated with the set
	 * of big integers at specified numerical key.
	 * @param key
	 * @return
	 */
	Optional<PData> get(long key);

	/**
	 * Select set/collection from the tracked
	 * info based upon a predicate provided.
	 * Expecting only 1 mactching collection
	 * maximumly.
	 *
	 * @param pred
	 * @return
	 */
	Optional<PData> select(LongPredicate pred);

	/**
	 * Log metrics/info regarding the tracked
	 * collections.
	 */
	void log();
}