package com.starcases.prime.cli;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.starcases.prime.base.api.PrimeBaseIntfc;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;


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

	/**
	 * default ctor
	 */
	private PTKFactory()
	{}
}
