package com.starcases.prime;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
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

//	static
//	{
		//try
	//	{
			//cacheMgr = new DefaultCacheManager("infinispan.xml");
			//cacheMgr.startCaches("primes", "primerefs");
			// "PREFIX", "PREFIX_TREE", "NPRIME", "THREETRIPLE" , "DEFAULT"
	//	}
	//	catch(IOException e)
	//	{
	//		System.out.println("couldn't create cache mgr from classpath:infinispan.xm");
	//	}
//	}

	@Getter
	@Setter
	private static @Min(1) long maxCount;

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
	private static @NonNull BiFunction<Long, Set<BigInteger>, PrimeRefIntfc> primeRefRawCtor;

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
					public BiFunction<Long, Set<BigInteger>, PrimeRefIntfc> getPrimeRefRawConstructor()
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
	static PrimeSourceIntfc primeSource(
			@Min(1) long maxCount,
			@Min(1) int confidenceLevel,
			@NonNull BiFunction<Long, Set<BigInteger>, PrimeRefIntfc> primeRefRawCtor,
			@NonNull List<Consumer<PrimeSourceIntfc>> consumersSetPrimeSource
			)
	{
		return new PrimeSource(maxCount
								, consumersSetPrimeSource
								, confidenceLevel
								,primeRefRawCtor
								, cacheMgr);
	}
}
