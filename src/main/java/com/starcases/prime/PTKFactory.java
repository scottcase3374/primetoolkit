package com.starcases.prime;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import com.starcases.prime.impl.CollectionTrackerImpl;
import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.immutable.ImmutableListFactoryImpl;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

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
@SuppressWarnings({"PMD.SystemPrintln"})
public final class PTKFactory
{
	/**
	 * manage any/all caching to files
	 */
	@Getter
	private static EmbeddedCacheManager cacheMgr;

	static
	{
		try
		{
			cacheMgr = new DefaultCacheManager("infinispan.xml");
			cacheMgr.startCaches("primes");
			// "PREFIX", "PREFIX_TREE", "NPRIME", "THREETRIPLE" , "DEFAULT"
		}
		catch(IOException e)
		{
			System.out.println("couldn't create cache mgr from classpath:infinispan.xm");
		}
	}

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
	private static @NonNull BiFunction<Long, MutableList<ImmutableLongCollection>, PrimeRefIntfc> primeRefRawCtor;

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
											getConsumersSetPrimeSrc());
					}

					@Override
					public Supplier<PrimeBaseIntfc> getPrimeBaseConstructor()
					{
						return primeBaseCtor;
					}

					@Override
					public BiFunction<Long, MutableList<ImmutableLongCollection>, PrimeRefIntfc> getPrimeRefRawConstructor()
					{
						return primeRefRawCtor;
					}

					@Override
					public ImmutableList<Consumer<PrimeSourceIntfc>> getConsumersSetPrimeSrc()
					{
						return ImmutableListFactoryImpl.INSTANCE.of(primeRefSetPrimeSource, baseSetPrimeSource);
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
			@NonNull final BiFunction<Long, MutableList<ImmutableLongCollection>, PrimeRefIntfc> primeRefRawCtor,
			@NonNull final ImmutableList<Consumer<PrimeSourceIntfc>> consumersSetPrimeSrc
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
