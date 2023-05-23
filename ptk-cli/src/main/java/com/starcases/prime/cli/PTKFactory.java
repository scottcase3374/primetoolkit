package com.starcases.prime.cli;

import java.nio.file.Path;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.cache.Cache;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import com.starcases.prime.base.api.PrimeBaseIntfc;
import com.starcases.prime.core.api.FactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.impl.PrimeSource;
import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.datamgmt.api.CollectionTrackerProviderIntfc;
import com.starcases.prime.preload.api.PreloaderIntfc;
import com.starcases.prime.preload.api.PreloaderProviderIntfc;
import com.starcases.prime.preload.api.PrimeSubset;
import com.starcases.prime.service.impl.SvcLoader;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.list.immutable.ImmutableListFactoryImpl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 *
 * factory providing defaults for some example usages.
 *
 */
@SuppressWarnings({"PMD.SystemPrintln"})
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
	private static @NonNull Function<Long, PrimeRefFactoryIntfc> primeRefRawCtor;

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
	private static @NonNull Supplier<PrimeBaseIntfc> primeBaseCtor;

	@Getter
	@Setter
	private static Path inputFolderPath;

	@Getter
	@Setter
	private static Cache<Long,PrimeSubset> cache;

	/**
	 * track collections of primes representing prefix prime sums - maintain single copy per unique set.
	 */
	@SuppressWarnings({"PMD.FieldNamingConventions"})
	@Getter
	private static final CollectionTrackerIntfc collTracker;

	static
	{
		final SvcLoader<CollectionTrackerProviderIntfc, Class<CollectionTrackerProviderIntfc>> collTreeProvider = new SvcLoader< >(CollectionTrackerProviderIntfc.class);
		final ImmutableCollection<String> colTreeAttributes = Lists.immutable.of("COLLECTION_TRACKER");
		collTracker = collTreeProvider
						.provider(colTreeAttributes)
						.map(p -> p.create(null))
						.orElse(null);
	}

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
					public PrimeSourceFactoryIntfc getPrimeSource(final boolean preload)
					{
						final PrimeSourceFactoryIntfc pSrc;
						if (preload)
						{
							pSrc = getPrimeSourceLoaded();
						}
						else
						{
							pSrc = getPrimeSourcePlain();
						}
						return pSrc;
					}

					private PrimeSourceFactoryIntfc getPrimeSourcePlain()
					{
						return primeSource(	maxCount,
								confidenceLevel,
								getPrimeRefRawConstructor(),
								getConsumersSetPrimeSrc(),
								collTracker,
								null);
					}

					private PrimeSourceFactoryIntfc getPrimeSourceLoaded()
					{
						final SvcLoader<PreloaderProviderIntfc, Class<PreloaderProviderIntfc>> preloadProvider = new SvcLoader< >(PreloaderProviderIntfc.class);
						final ImmutableCollection<String> attributes = Lists.immutable.of("PRELOADER");

						final PreloaderIntfc primePreloader = preloadProvider
								.provider(attributes)
								.map(p -> p.create(cache, inputFolderPath, null))
								.orElse(null);

						return primeSource(	maxCount,
								confidenceLevel,
								getPrimeRefRawConstructor(),
								getConsumersSetPrimeSrc(),
								collTracker,
								primePreloader);
						}

					@Override
					public Supplier<PrimeBaseIntfc> getPrimeBaseConstructor()
					{
						return primeBaseCtor;
					}

					@Override
					public Function<Long, PrimeRefFactoryIntfc> getPrimeRefRawConstructor()
					{
						return primeRefRawCtor;
					}

					@Override
					public ImmutableList<Consumer<PrimeSourceIntfc>> getConsumersSetPrimeSrc()
					{
						return ImmutableListFactoryImpl.INSTANCE.of(primeRefSetPrimeSource);
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
	private static PrimeSourceFactoryIntfc primeSource(
			@Min(1) final long maxCount,
			@Min(1) final int confidenceLevel,
			@NonNull final Function<Long, PrimeRefFactoryIntfc> primeRefRawCtor,
			@NonNull final ImmutableList<Consumer<PrimeSourceIntfc>> consumersSetPrimeSrc,
			final CollectionTrackerIntfc collTree,
			final PreloaderIntfc preloader
			)
	{
		return new PrimeSource(maxCount
								, consumersSetPrimeSrc
								, confidenceLevel
								,primeRefRawCtor,
								collTree,
								preloader
								);
	}
}
