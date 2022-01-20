package com.starcases.prime.intfc;

import java.util.BitSet;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface FactoryIntfc
{
	PrimeSourceIntfc getPrimeSource();
	Supplier<PrimeBaseIntfc> getPrimeBaseConstructor();
	BiFunction<Integer, BitSet, PrimeRefIntfc> getPrimeRefConstructor();
}
