package com.starcases.prime.intfc;

import java.util.Optional;
import java.util.function.LongPredicate;

import com.starcases.prime.impl.PData;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;

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
	 * Specify set of primes to track as group.
	 * @param sp1
	 * @return
	 */
	PData track(ImmutableLongCollection sp1);

	/**
	 * Get tracking information associated with the set
	 * of primes at specified numerical key.
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