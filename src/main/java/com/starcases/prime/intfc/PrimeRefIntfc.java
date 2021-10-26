package com.starcases.prime.intfc;

import java.util.NavigableSet;

public interface PrimeRefIntfc<E> extends Comparable<PrimeRefIntfc<E>>
{
	NavigableSet<NavigableSet<PrimeRefIntfc<E>>> getPrimeBase();
	E getPrime();
	
}
