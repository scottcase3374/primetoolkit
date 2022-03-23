package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * This interface is a helper for the CLI support - helps simply the
 * constructions of a valid set of actions from the command line
 * arguments.
 *
 */
public interface FactoryIntfc
{
	PrimeSourceIntfc getPrimeSource();
	Supplier<PrimeBaseIntfc> getPrimeBaseConstructor();
	BiFunction<Integer, Set<Integer>, PrimeRefIntfc> getPrimeRefConstructor();
	BiFunction<Integer, Set<BigInteger>, PrimeRefIntfc> getPrimeRefRawConstructor();
}
