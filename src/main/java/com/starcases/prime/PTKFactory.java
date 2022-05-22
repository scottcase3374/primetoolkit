package com.starcases.prime;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.starcases.prime.impl.CollectionTrackerImpl;
import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import com.starcases.prime.intfc.CollectionTrackerIntfc;
import com.starcases.prime.intfc.FactoryIntfc;
import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;

/**
 *
 * factory providing defaults for some example usages.
 *
 */
@SuppressWarnings({"PMD.LongVariable", "PMD.CommentSize"})
public final class PTKFactory
{
	/**
	 * Maximum number of primes to handle.
	 */
	@Getter
	@Setter
	private static @Min(1) long maxCount;

	/**
	 * ceiling value for the set of primes to reduce to.
	 */
	@Getter
	@Setter
	private static @Max(3) int maxReduce;

	/**
	 * apache confidence level of resulting prime check.
	 */
	@Getter
	@Setter
	private static @Min(1) int confidenceLevel;

	/**
	 * helper for constructing primeref instances.
	 */
	@Getter
	@Setter
	private static @NonNull BiFunction<Long, List<Set<BigInteger>>, PrimeRefIntfc> primeRefRawCtor;

	/**
	 * helper for assigning the primesrc to prime ref.
	 */
	@Getter
	@Setter
	private static @NonNull Consumer<PrimeSourceIntfc> primeRefSetPrimeSource;

	/**
	 * helper for assigning the primesrc
	 */
	@Getter
	@Setter
	private static @NonNull Consumer<PrimeSourceIntfc> baseSetPrimeSource;

	/**
	 * helper for assigning the primesrc
	 */
	@Getter
	@Setter
	private static @NonNull Supplier<PrimeBaseIntfc> primeBaseCtor;

	/**
	 * track collections of primes representing prefix prime sums - maintain single copy per unique set.
	 */
	@Getter
	private static @NonNull CollectionTrackerIntfc collTrack = new CollectionTrackerImpl();

	/**
	 * default ctor
	 */
	private PTKFactory()
	{}

	/**
	 * return factory instance to use.
	 * @return
	 */
	public static FactoryIntfc getFactory()
	{
		return new FactoryIntfc()
				{
					@Override
					public PrimeSourceIntfc getPrimeSource()
					{
						return primeSource(	maxCount,
											confidenceLevel,
											getPrimeRefRawConstructor(),
											List.of(primeRefSetPrimeSource, baseSetPrimeSource));
					}

					@Override
					public Supplier<PrimeBaseIntfc> getPrimeBaseConstructor()
					{
						return primeBaseCtor;
					}

					@Override
					public BiFunction<Long, List<Set<BigInteger>>, PrimeRefIntfc> getPrimeRefRawConstructor()
					{
						return primeRefRawCtor;
					}
				};
	}

	/**
	 * Use alternative impl of PrimeRef based on interface PrimeRefIntfc
	 *
	 * @param maxCount
	 * @param confidenceLevel
	 * @param primeRefCtor
	 * @param primeRefSetPrimeSource
	 * @param baseSetPrimeSource
	 * @return
	 */
	private static PrimeSourceIntfc primeSource(
			@Min(1) final long maxCount,
			@Min(1) final int confidenceLevel,
			@NonNull final BiFunction<Long, List<Set<BigInteger>>, PrimeRefIntfc> primeRefRawCtor,
			@NonNull final List<Consumer<PrimeSourceIntfc>> consumersSetPrimeSrc
			)
	{
		return new PrimeSource(maxCount
								, consumersSetPrimeSrc
								, confidenceLevel
								,primeRefRawCtor,
								collTrack
								);
	}
}
