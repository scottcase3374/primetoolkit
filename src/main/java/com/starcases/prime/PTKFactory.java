package com.starcases.prime;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import com.starcases.prime.intfc.FactoryIntfc;
import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;

/**
 *
 * factory providing defaults for some example usages.
 *
 */

public class PTKFactory
{
	@Getter
	private static EmbeddedCacheManager cacheMgr;

	static
	{
		try
		{
			cacheMgr = new DefaultCacheManager("infinispan.xml");
			cacheMgr.startCaches("primes", "primerefs");
			// "PREFIX", "PREFIX_TREE", "NPRIME", "THREETRIPLE" , "DEFAULT"
		}
		catch(IOException e)
		{
			System.out.println("couldn't create cache mgr from classpath:infinispan.xm");
		}
	}

	@Getter
	@Setter
	private static @Min(1) int maxCount;

	@Getter
	@Setter
	private static @Max(3) int maxReduce;

	@Getter
	@Setter
	private static @Min(1) int confidenceLevel;

	@Getter
	@Setter
	private static @NonNull BaseTypes activeBaseId;

	@Getter
	@Setter
	private static @NonNull BiFunction<Integer, List<Integer>, PrimeRefIntfc> primeRefCtor;

	@Getter
	@Setter
	private static @NonNull Consumer<PrimeSourceIntfc> primeRefSetPrimeSource;

	@Getter
	@Setter
	private static @NonNull Consumer<PrimeSourceIntfc> baseSetPrimeSource;

	@Getter
	@Setter
	private static @NonNull Supplier<PrimeBaseIntfc> primeBaseCtor;

	private PTKFactory()
	{}

	public static FactoryIntfc getFactory()
	{
		return new FactoryIntfc()
				{
					@Override
					public PrimeSourceIntfc getPrimeSource()
					{
						return primeSource(maxCount, confidenceLevel, getPrimeRefConstructor(), primeRefSetPrimeSource, baseSetPrimeSource);
					}

					@Override
					public Supplier<PrimeBaseIntfc> getPrimeBaseConstructor()
					{
						return primeBaseCtor;
					}

					@Override
					public BiFunction<Integer, List<Integer>, PrimeRefIntfc> getPrimeRefConstructor()
					{
						return primeRefCtor;
					}
				};
	}

	/**
	 * Use alternative impl of PrimeRef based on interface PrimeRefIntfc
	 *
	 * @param maxCount
	 * @param confidenceLevel
	 * @param activeBaseId
	 * @param primeRefCtor
	 * @param primeRefSetPrimeSource
	 * @return
	 */
	static PrimeSourceIntfc primeSource(
			@Min(1) int maxCount,
			@Min(1) int confidenceLevel,
			@NonNull BiFunction<Integer, List<Integer>, PrimeRefIntfc> primeRefCtor,
			@NonNull Consumer<PrimeSourceIntfc> consumerSetPrimeSource,
			@NonNull Consumer<PrimeSourceIntfc> baseSetPrimeSource
			)
	{
		return new PrimeSource(maxCount
								,primeRefCtor
								, consumerSetPrimeSource
								, baseSetPrimeSource
								, confidenceLevel
								, cacheMgr);
	}
}
