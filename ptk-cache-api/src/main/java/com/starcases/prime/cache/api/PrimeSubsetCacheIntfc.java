package com.starcases.prime.cache.api;

import javax.cache.Cache;

import com.starcases.prime.cache.api.subset.PrimeSubsetIntfc;

import lombok.NonNull;

public interface PrimeSubsetCacheIntfc<K> extends Cache<K,PrimeSubsetIntfc>
{
	void persistAll();

	void persist(@NonNull final K keyVal, @NonNull final PrimeSubsetIntfc subsetVal);

	/**
	 * These return prime/bases; parameters tend to be or operate on indexes unless otherwise specified
	 */
	/*
	 * RichIterable<RichIterable<V>> chunk(int size); <R> MutableBag<V>
	 * collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R>
	 * function);
	 *
	 * MutableLongBag collectLong(LongFunction<? super V> longFunction);
	 * MapIterable<K,V> ifPresentApply(K key, Function<? super V, ? extends Object>
	 * function);
	 *
	 * long getIfAbsent(long idx, long def); long reduceIfEmpty(@NonNull final
	 * LongLongToLongFunction fn, final long primeDefault); long
	 * reduceIfEmpty(@NonNull final LongLongToLongFunction fn); MutableLongLongMap
	 * select(@NonNull final LongLongPredicate keyValPred); <R> R select(@NonNull
	 * final LongPredicate pred, @NonNull final R target);
	 */



}