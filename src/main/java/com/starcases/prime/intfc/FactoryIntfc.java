package com.starcases.prime.intfc;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.MutableList;

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
	BiFunction<Long, MutableList<ImmutableLongCollection>, PrimeRefIntfc> getPrimeRefRawConstructor();
}
