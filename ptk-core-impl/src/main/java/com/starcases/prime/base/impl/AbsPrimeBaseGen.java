package com.starcases.prime.base.impl;

import java.util.BitSet;
import java.util.Optional;
import java.util.OptionalLong;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;

import com.starcases.prime.base.api.BaseGenFactoryIntfc;
import com.starcases.prime.cache.api.PersistedPrefixCacheIntfc;
import com.starcases.prime.cache.api.subset.PrefixSubsetIntfc;
import com.starcases.prime.cache.api.subset.PrefixSubsetProviderIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.Arrays;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.kern.api.IdxToSubsetMapperIntfc;
import com.starcases.prime.kern.api.Permutation;
import com.starcases.prime.kern.impl.IdxToSubsetMapperImpl;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 *
 * Abstract class for common Prime base functionality.
 *
 */
public abstract class AbsPrimeBaseGen implements BaseGenFactoryIntfc
{
	private static final IdxToSubsetMapperIntfc idxMap = new IdxToSubsetMapperImpl();

	private final PrefixSubsetProviderIntfc prefixSubsetProvider = new SvcLoader<PrefixSubsetProviderIntfc, Class<PrefixSubsetProviderIntfc>>(PrefixSubsetProviderIntfc.class)
			.provider(Lists.immutable.of("PREFIX_SUBSET")).orElseThrow();

	protected long [] lastSubset = {0};
	protected int [] lastOffset = {0};
	protected MutableList<Long[]> subsetColl = MutableListFactoryImpl.INSTANCE.empty();

	@Getter
	private final BaseTypesIntfc baseType;

	protected final Optional<PersistedPrefixCacheIntfc> optCache;
	/**
	 * Access to lookup of prime/primerefs and the init of base information.
	 */
	@Getter(AccessLevel.PROTECTED)
	protected PrimeSourceIntfc primeSrc;

	/**
	 * Indicator for whether a sum is over/under/equal desired value or must-undo change
	 */
	protected enum State { OVER, UNDER, EQUAL, REVERT }

	/**
	 * Flag indicating whether base construction can use multiple CPU cores
	 */
	@Getter(AccessLevel.PROTECTED)
	protected boolean preferParallel;


	/**
	 * Constructor for secondary bases.
	 * @param primeSrc
	 */
	protected AbsPrimeBaseGen(@NonNull final BaseTypesIntfc baseType)
	{
		this.baseType = baseType;
		this.optCache = Optional.empty();
	}

	/**
	 * Constructor for secondary bases.
	 * @param primeSrc
	 */
	protected <K,V> AbsPrimeBaseGen(@NonNull final BaseTypesIntfc baseType, PersistedPrefixCacheIntfc cache)
	{
		this.baseType = baseType;
		this.optCache = Optional.ofNullable(cache);
	}

	/**
	 * fluent style method for setting flag for whether base construction can use multiple CPU cores.
	 * @param preferParallel
	 * @return
	 */
	@Override
	public BaseGenFactoryIntfc doPreferParallel(final boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	@Override
	public BaseGenFactoryIntfc assignPrimeSrc(final PrimeSourceIntfc primeSrc)
	{
		this.primeSrc = primeSrc;
		return this;
	}

	protected ImmutableLongList findPrefixes2(final PrimeRefIntfc curPrime)
	{
		final MutableLongList bases = LongLists.mutable.of();

		if (curPrime.getPrime() - 2 <= 0)
		{
			bases.add(curPrime.getPrime());
		}
		else
		{
			long topIndex = curPrime.getPrimeRefIdx();
			long remain = -1;
			Optional<PrimeRefIntfc> topPrime = Optional.of(curPrime);

			while (topIndex > 0 && topPrime.isPresent() && remain != 0)
			{
				bases.clear();
				remain = curPrime.getPrime();
				long prevIndex = topIndex--;
				boolean doContinue;

				do
				{
					final long prevPrime = primeSrc.getPrimeForIdx(--prevIndex).getAsLong();

					final long tmpRemain = remain - prevPrime;
					if (tmpRemain >= 0)
					{
						remain = tmpRemain;
						bases.add(prevPrime);
					}
					doContinue = prevIndex > 0 && remain > 0;
				}
				while(doContinue);

				topPrime = topPrime.get().getPrevPrimeRef();
			}
		}

		var immutBases = bases.toImmutable();
		cacheBase(curPrime, bases);
		return immutBases;
	}



	private void cacheBase(final PrimeRefIntfc curPrime, final MutableLongList bases)
	{
		optCache.ifPresent(
				curCache -> {
						final long[] subsetIdx = {0};
						final int [] offsetIdx = {0};
						idxMap.convertIdxToSubsetAndOffset(curPrime.getPrimeRefIdx(), subsetIdx, offsetIdx);

						// Cache completed subset
						if (this.lastSubset[0] != subsetIdx[0])
						{
							persistSubset(curCache);
							this.lastSubset[0] = subsetIdx[0];
						}

						subsetColl.add(Arrays.longArrayToLongArray(bases.toArray()));
					}
				);
	}

	private void persistSubset(final PersistedPrefixCacheIntfc curCache)
	{
		final long [][] tmpBases = new long[subsetColl.size()][];
		for (int i=0; i< subsetColl.size(); i++)
		{
			tmpBases[i] = Arrays.longArrayToLongArray(subsetColl.get(i));
		}
		final PrefixSubsetIntfc subsetInst = prefixSubsetProvider.create(tmpBases);
		curCache.persist(this.lastSubset[0], subsetInst);

		subsetColl.clear();
	}

	/**
	 * Persist any final partial subset.
	 */
	public void persistFinalSubset()
	{
		optCache.ifPresent(this::persistSubset);
	}

	record Data(PrimeRefIntfc base, long remain) {}

	/**
	 * Should produce the longest prefix due to starting with lowest values first.
	 *
	 * @param tgtPrime
	 * @return
	 */
	protected ImmutableLongList findPrefixesLowFirst(final PrimeRefIntfc tgtPrime)
	{
		final MutableLongList bases = LongLists.mutable.of();

		if (tgtPrime.getPrime() - 2 <= 0)
		{
			bases.add(tgtPrime.getPrime());
		}
		else
		{
			final Optional<PrimeRefIntfc> topPrime = primeSrc.getPrimeRefForIdx(tgtPrime.getPrimeRefIdx()-1);
			bases.add(topPrime.get().getPrime());

			final var remain = tgtPrime.getPrime() - topPrime.get().getPrime();

			// n-bit permutation starting with value of 0
			final var primeIndexPermutation = new BitSet();

			boolean done = false;
			do
			{
				// Check this against the target Prime.
				final var permutationSum = primeIndexPermutation
						.stream()
						.mapToObj(primeSrc::getPrimeForIdx)
						.filter(OptionalLong::isPresent)
						.map(OptionalLong::getAsLong)
						.reduce(0L, (a, b) -> a+b);

				if (permutationSum == remain)
				{
					primeIndexPermutation
					.stream()
					.mapToObj(primeSrc::getPrimeForIdx)
					.filter(OptionalLong::isPresent)
					.map(OptionalLong::getAsLong)
					.forEach(bases::add);
					done = true;
				}
				else
				{
					if (primeIndexPermutation.size() <= 62)
					{
						Permutation.incrementPermutation(primeIndexPermutation);
					}
					else
					{
						// ensure we don't increment forever
						done = true;
					}
				}
			}
			while (!done);
		}

		var immutBases = bases.toImmutable();
		cacheBase(tgtPrime, bases);
		return immutBases;
	}
}
