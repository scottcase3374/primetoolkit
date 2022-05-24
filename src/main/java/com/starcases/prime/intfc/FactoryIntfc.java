package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * This interface is a helper for the CLI support - helps simplify the
 * constructions of a valid set of actions from the command line
 * arguments.
 *
 */
public interface FactoryIntfc
{
	/**
	 * Get prime src ref.
	 * provide prime/primeref lookups
	 * @return
	 */
	PrimeSourceIntfc getPrimeSource();

	/**
	 * Get a supplier of the base constructor.
	 * Provides a means of configuring system differently
	 * Need better desc.
	 * @return
	 */
	Supplier<PrimeBaseIntfc> getPrimeBaseConstructor();

	/**
	 * Provides a means of configuring system differently.
	 * Need better desc.
	 * @return
	 */
	BiFunction<Long, List<Set<BigInteger>>, PrimeRefIntfc> getPrimeRefRawConstructor();
}
