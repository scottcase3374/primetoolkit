package com.starcases.prime.datamgmt.impl;

import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableLongObjectMapFactoryImpl;

import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.datamgmt.api.CollectionTreeIteratorIntfc;
import com.starcases.prime.datamgmt.api.CollectionTreeNode;
import com.starcases.prime.datamgmt.api.PData;
import com.starcases.prime.logging.PTKLogger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public class CollectionTrackerImpl implements CollectionTrackerIntfc
{
	/**
	 * prefix map
	 */
	@Getter
	private final MutableLongObjectMap<CollectionTreeNode> prefixMap =  MutableLongObjectMapFactoryImpl.INSTANCE.empty();

	/**
	 * Track source primes (tree/prefix)
	 */
	@Getter(AccessLevel.PRIVATE)
	private final ConcurrentMutableMap<Long, PData> sourcePrimes = new ConcurrentHashMap<>(500);

	/**
	 * Track counts of each source prime entry
	 */
	@Getter(AccessLevel.PRIVATE)
	private final ConcurrentMutableMap<Long, Long> useCounts = new ConcurrentHashMap<>(500);

	/**
	 * container for tracking the unique set of prefixes/trees
	 */
	//@Getter(AccessLevel.PRIVATE)
	//private final CollectionTrackerIntfc collectionTracker;

	/**
	 * Constructor for the collection tree
	 * @param collectionTracker
	 */
	public CollectionTrackerImpl()
	{}

//	public CollectionTreeImpl(@NonNull final CollectionTrackerIntfc collectionTracker)
//	{
//		this.collectionTracker = collectionTracker;
//	}

	/**
	 * get iterator to the tree info
	 * @return
	 */
	@Override
	public CollectionTreeIteratorIntfc iterator()
	{
		return new CollectionTreeIterator(this);
	}

	/**
	 * Execute 'func' against the prime tree data and return a PData
	 * if the predicate is matched. Looking for key for
	 * a prime sum that when added to a new prime results in a new prime.
	 *
	 * NOTE: Desire lowest matching value found
	 *
	 * @param biFunc
	 * @return
	 */
//	@Override
//	public Optional<PData> select(final LongPredicate pred)
//	{
//		return collectionTracker.select(pred);
//	}

	/**
	 * output logging for collection tracking.
	 */
	@Override
	public void log()
	{
		PTKLogger.output("%nCollection tracking:%n", "");
		useCounts.forEach(
				(l1, l2) ->
					PTKLogger.output("sum: [%d] occurrences: [%d] prefix: [%s]%n",
							l1,
							l2,
							sourcePrimes.get(l1).toCanonicalCollection().makeString()));
	}

	/**
	 * Track the specified collection
	 */
	@Override
	public PData track(@NonNull final ImmutableLongCollection collection)
	{
		final long sum = collection.sum();
		useCounts.compute(sum, (l1, l2) -> l2 != null? l2+1 : 0);
		return sourcePrimes.computeIfAbsent(sum, k -> new PData(collection, sum));
	}

	/**
	 * Select an existing collection based upon the provided
	 * predicate
	 */
	@Override
	public Optional<PData> select(@NonNull final LongPredicate pred)
	{
		final Predicate<Entry<Long,PData>> pred1 = p -> pred.test(p.getKey());
		final var ret = sourcePrimes.entrySet()
				.stream()
				.filter(pred1)
				.min((x,y) -> x.getKey().compareTo(y.getKey()) )
				.map(Entry::getValue);
		ret.ifPresent(pd -> useCounts.compute(pd.prime(), (l1, l2) -> l2 != null ? l2+1 : 0));
		return ret;
	}

	/**
	 * get the tracked data for the specified key.
	 */
	@Override
	public Optional<PData> get(final long key)
	{
		return Optional.ofNullable(sourcePrimes.get(key));
	}
}
