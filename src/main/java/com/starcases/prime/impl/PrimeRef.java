package com.starcases.prime.impl;

import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import com.starcases.prime.intfc.PrimeComparatorIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;

/**
* General algorithm uses powerset of processed primes as source of potential base sets for next prime. 
* The general idea being that any future prime should be representable through the sum of a subset of the
* previous primes.
* 
* As as second phase of this; after seeing how many primes are found in a single pass but only 1 retained;
* looking to retain/return those primes without additional processing.
**/

public class PrimeRef<E extends Number & Comparable<E>> implements PrimeRefIntfc<E>
{
	//
	// Instance data
	//
	
	// This instance of a prime
	E prime;
	
	// Represents sets of base primes that sum to this prime.
	NavigableSet<NavigableSet<PrimeRefIntfc<E>>> primeBases; 
	
	/**
	 * Handle simple prime where the base is simply itself - i.e. 1, 2 
	 * Simplifies bootstrapping
	 * 
	 * @param prime
	 */
	PrimeRef(E prime, PrimeComparatorIntfc<E> comparators)
	{
		this.prime = prime;
		primeBases = new ConcurrentSkipListSet<>(comparators.getPrimeSetComparator());
		NavigableSet<PrimeRefIntfc<E>> pfc = new ConcurrentSkipListSet<>(comparators.getPrimeFactorComparator());
		pfc.add(this);		
		addPrimeBase(pfc);
	} 
	
	PrimeRef(E prime, NavigableSet<PrimeRefIntfc<E>> base, PrimeComparatorIntfc<E> comparators)
	{
		this.prime = prime;
		primeBases = new ConcurrentSkipListSet<>(comparators.getPrimeSetComparator());
		addPrimeBase(base);
	}

	public E getPrime()
	{
		return prime;
	}
	
	/**
	 * Include a set of primes in the set of prime bases for the current prime.
	 * @param primeBase
	 */
	void addPrimeBase(NavigableSet<PrimeRefIntfc<E>> primeBase)
	{
		this.primeBases.add(primeBase);
	}
	
	public NavigableSet<NavigableSet<PrimeRefIntfc<E>>> getPrimeBase()
	{
		return primeBases;
	}
	
	/**
	 * Override implements Comparable<PrimeRefIntfc>
	 * Natural order based on the Longs used to represent the primes.
	 */
	@Override
	public int compareTo(PrimeRefIntfc<E> o) 
	{	
		int ret;
		
		if (this == o)
			ret = 0;
		else if (o == null)
			ret = 1;
		else 
			ret = (int) (this.getPrime().compareTo(o.getPrime()));
		return ret;
	}		
	
	public boolean equals(Object o)
	{
		if (o == this)
			return true;
		else if (o == null)
			return false;
		else if (!(o instanceof PrimeRef))
			return false;
		if (hashCode() == o.hashCode())
			return true;
		return false;		
	}
	
	public int hashCode()
	{
		return (int)prime;
	}
}
