package com.starcases.prime.core.api;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.base.api.PrimeBaseIntfc;

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
	PrimeSourceFactoryIntfc getPrimeSource(final boolean preload);

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
	Function<Long, PrimeRefFactoryIntfc> getPrimeRefRawConstructor();

	/**
	 * Provides a means of providing alternative configuration via different factories
	 *
	 * @return
	 */
	ImmutableList<Consumer<PrimeSourceIntfc>> getConsumersSetPrimeSrc();
}
