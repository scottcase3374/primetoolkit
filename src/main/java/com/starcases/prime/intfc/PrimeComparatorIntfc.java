package com.starcases.prime.intfc;

import java.util.Comparator;
import java.util.NavigableSet;

public interface PrimeComparatorIntfc<E>
{
	Comparator<PrimeRefIntfc<E>> getPrimeFactorComparator();
	Comparator<NavigableSet<PrimeRefIntfc<E>>> getPrimeSetComparator();
}
