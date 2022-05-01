package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import com.starcases.prime.intfc.CollectionTrackerIntfc;


public class CollectionTrackerImpl implements CollectionTrackerIntfc
{
	private final ConcurrentNavigableMap<Long, PData> sourcePrimes = new ConcurrentSkipListMap<>();

	@Override
	public PData track(Set<BigInteger> collection)
	{
		var sum = collection.stream().reduce(BigInteger.ZERO, BigInteger::add).longValue();
		return sourcePrimes.computeIfAbsent(sum, k -> new PData(collection, sum));
	}

	@Override
	public Optional<PData> select(LongPredicate pred)
	{
		Predicate<Entry<Long,PData>> pred1 = p -> pred.test(p.getKey());
		return sourcePrimes.entrySet()
				.stream()
				.filter(pred1)
				.findAny()
				.map(Entry::getValue);
	}
}
