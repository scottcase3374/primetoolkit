package com.starcases.prime.datamgmt.impl;

import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableLongObjectMapFactoryImpl;

import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.datamgmt.api.CollectionTreeIterator;
import com.starcases.prime.datamgmt.api.CollectionTreeIteratorIntfc;
import com.starcases.prime.datamgmt.api.PData;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;
import com.starcases.prime.datamgmt.api.CollectionTreeNode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

class CollectionTrackerImpl implements CollectionTrackerIntfc
{
	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
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
	 * Constructor for the collection tree
	 * @param collectionTracker
	 */
	public CollectionTrackerImpl()
	{
		// Nothing to do
	}

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
	 * output logging for collection tracking.
	 */
	@Override
	public void log()
	{
		statusHandler.output("%nCollection tracking:%n", "");
		useCounts.forEach(
				(l1, l2) ->
				statusHandler.output("sum: [%d] occurrences: [%d] prefix: [%s]%n",
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
