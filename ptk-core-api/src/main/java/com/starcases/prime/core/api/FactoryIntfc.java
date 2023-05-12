package com.starcases.prime.core.api;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;

import com.starcases.prime.base.api.PrimeBaseIntfc;
import com.starcases.prime.preload.api.PreloaderIntfc;

/* simplify the
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
	PrimeSourceFactoryIntfc getPrimeSource();

	PrimeSourceFactoryIntfc getPrimeSource(final PreloaderIntfc preprimed);

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

	/**
	 * Provides a means of providing alternative configuration via different factories
	 *
	 * @return
	 */
	ImmutableList<Consumer<PrimeSourceIntfc>> getConsumersSetPrimeSrc();
}