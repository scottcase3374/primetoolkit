package com.starcases.prime;

import java.util.BitSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.validation.constraints.Min;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.impl.PrimeRef;
import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

import com.starcases.prime.intfc.PrimeRefIntfc;

/**
 *
 * factory providing defaults for some example usages.
 *
 */
public class PrimeSourceFactory
{
	private PrimeSourceFactory()
	{}

	/**
	 * Use default PrimeRef impl
	 *
	 * @param maxCount
	 * @param confidenceLevel
	 * @param activeBaseId
	 * @return
	 */
	public static PrimeSourceIntfc primeSource(
			@Min(1) int maxCount,
			@Min(1) int confidenceLevel,
			@NonNull BaseTypes activeBaseId)
	{
		var ps = new PrimeSource(maxCount, confidenceLevel, null, PrimeRef::setPrimeSource);
		ps.setActiveBaseId(activeBaseId);
		return ps;
	}

	/**
	 * Use default PrimeRef impl
	 *
	 * @param maxCount
	 * @param confidenceLevel
	 * @return
	 */
	public static PrimeSourceIntfc primeSource(
			@Min(1) int maxCount,
			@Min(1) int confidenceLevel)
	{
		return new PrimeSource(maxCount, confidenceLevel, null, PrimeRef::setPrimeSource);
	}

	/**
	 * Use alternative impl of PrimeRef based on interface PrimeRefIntfc
	 *
	 * @param maxCount
	 * @param confidenceLevel
	 * @param activeBaseId
	 * @param primeRefCtor
	 * @param fnSetPrimeSource
	 * @return
	 */
	public static PrimeSourceIntfc primeSource(
			@Min(1) int maxCount,
			@Min(1) int confidenceLevel,
			@NonNull BaseTypes activeBaseId,
			@NonNull BiFunction<Integer, BitSet, PrimeRefIntfc> primeRefCtor,
			@NonNull Consumer<PrimeSourceIntfc> fnSetPrimeSource)
	{
		var ps = new PrimeSource(maxCount, confidenceLevel, primeRefCtor, fnSetPrimeSource);
		ps.setActiveBaseId(activeBaseId);
		return ps;
	}

	/**
	 * Use alternative impl of PrimeRef based on interface PrimeRefIntfc
	 *
	 * @param maxCount
	 * @param confidenceLevel
	 * @param primeRefCtor
	 * @param fnSetPrimeSource
	 * @return
	 */
	public static PrimeSourceIntfc primeSource(
			@Min(1) int maxCount,
			@Min(1) int confidenceLevel,
			@NonNull BiFunction<Integer, BitSet, PrimeRefIntfc> primeRefCtor,
			@NonNull Consumer<PrimeSourceIntfc> fnSetPrimeSource)
	{
		return new PrimeSource(maxCount, confidenceLevel, primeRefCtor, fnSetPrimeSource);
	}
}
