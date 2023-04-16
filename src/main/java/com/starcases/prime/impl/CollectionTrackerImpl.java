package com.starcases.prime.impl;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.intfc.CollectionTrackerIntfc;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * manage handing out consistent references to the same copy of
 * collections when given the same members to prevent multiple
 * copies of large collections.
 *
 * Single-level structure maps a sum of prime collection to a structure
 * referencing the collection and the sum.
 */
@ApplicationScoped
public class CollectionTrackerImpl implements CollectionTrackerIntfc
{
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
	 * Track the specified collection
	 */
	@Override
	public PData track(final ImmutableLongCollection collection)
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
	public Optional<PData> select(final LongPredicate pred)
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

	/**
	 * output logging for collection tracking.
	 */
	@Override
	public void log()
	{
		PrimeToolKit.output("%nCollection tracking:%n", "");
		useCounts.forEach(
				(l1, l2) ->
					PrimeToolKit.output("sum: [%d] occurrences: [%d] prefix: [%s]%n",
							l1,
							l2,
							sourcePrimes.get(l1).toCanonicalCollection().makeString()));
	}
}
