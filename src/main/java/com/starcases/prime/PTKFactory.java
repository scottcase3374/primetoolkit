package com.starcases.prime;

import java.util.BitSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

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
	public static @Min(1) int maxCount;
	public static @Max(3) int maxReduce;
	public static @Min(1) int confidenceLevel;

	public static @NonNull BaseTypes activeBaseId;

	public static @NonNull BiFunction<Integer, BitSet, PrimeRefIntfc> primeRefCtor;

	public static @NonNull Consumer<PrimeSourceIntfc> primeRefSetPrimeSource;
	public static @NonNull Consumer<PrimeSourceIntfc> baseSetPrimeSource;

	public static @NonNull Supplier<PrimeBaseIntfc> primeBaseCtor;



	private PTKFactory()
	{}

	public static FactoryIntfc getFactory()
	{
		return new FactoryIntfc()
				{
					@Override
					public PrimeSourceIntfc getPrimeSource()
					{
						return primeSource(maxCount, confidenceLevel, activeBaseId, getPrimeRefConstructor(), primeRefSetPrimeSource, baseSetPrimeSource);
					}

					@Override
					public Supplier<PrimeBaseIntfc> getPrimeBaseConstructor()
					{
						return primeBaseCtor;
					}

					@Override
					public BiFunction<Integer, BitSet, PrimeRefIntfc> getPrimeRefConstructor()
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
			BaseTypes activeBaseId,
			@NonNull BiFunction<Integer, BitSet, PrimeRefIntfc> primeRefCtor,
			@NonNull Consumer<PrimeSourceIntfc> consumerSetPrimeSource,
			@NonNull Consumer<PrimeSourceIntfc> baseSetPrimeSource
			)
	{
		var ps = new PrimeSource(maxCount, confidenceLevel, primeRefCtor, consumerSetPrimeSource, baseSetPrimeSource);
		ps.setActiveBaseId(activeBaseId);
		return ps;
	}
}
