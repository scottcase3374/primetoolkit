package com.starcases.prime.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import org.apache.commons.math3.primes.Primes;

import com.starcases.prime.intfc.PrimeComparatorIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;

@Log
public class PrimeSource<E extends Number & Comparable<E>> implements PrimeSourceIntfc<E>
{
	//
	// Default / bootstrap data
	//		
	private PrimeRefIntfc<E> pf0;
	private PrimeRefIntfc<E> pf1;
	private PrimeRefIntfc<E> pf2;

	private PrimeRefIntfc<E> lastMaxPrime;

	// Comparison methods
	private PrimeComparatorIntfc<E> primeCompares;

	// Primes output and primes generated
	private NavigableSet<PrimeRefIntfc<E>> outputPrimes;	
	private NavigableSet<PrimeRefIntfc<E>> newPrimes;
	
	// sets of previous primes used to derive next primes
	private NavigableSet<NavigableSet<PrimeRefIntfc<E>>> superSet;

	public PrimeSource(PrimeComparatorIntfc<E> primeCompares)
	{	
		this.primeCompares = primeCompares;
		
		// Bootstrap
		pf0 = new PrimeRef(0L, primeCompares);
		pf1 = new PrimeRef(Long.valueOf(1L), primeCompares);
		pf2 = new PrimeRef(Long.valueOf(2L), primeCompares);
		
		outputPrimes = new ConcurrentSkipListSet<>(primeCompares.getPrimeFactorComparator());
		newPrimes = new ConcurrentSkipListSet<>(primeCompares.getPrimeFactorComparator());
		superSet = new ConcurrentSkipListSet<>(primeCompares.getPrimeSetComparator());
		
		newPrimes.add(pf1);
		updateSuperSet(pf1);
		
		newPrimes.add(pf2);
		updateSuperSet(pf2);
		 lastMaxPrime = pf0;
	}

	/**
	 * Add new prime to shared set of all primes
	 * @param aPrime
	 */
	PrimeRefIntfc<E> addPrime(PrimeRefIntfc<E> aPrime)
	{	
		outputPrimes.add(aPrime);
		newPrimes.remove(aPrime);
		
		lastMaxPrime = aPrime;	
		return lastMaxPrime;
	}	
	
	void updateSuperSet(PrimeRefIntfc<E> aPrime)
	{
		ConcurrentSkipListSet<NavigableSet<PrimeRefIntfc<E>>> newSets = new ConcurrentSkipListSet<>(primeCompares.getPrimeSetComparator());
		superSet.parallelStream().forEach(aSet -> { 
													NavigableSet<PrimeRefIntfc<E>> pfs = new ConcurrentSkipListSet<>(aSet);													
													pfs.add(aPrime);												
													newSets.add(pfs);	
													});
		
		NavigableSet<PrimeRefIntfc<E>> pfs = new ConcurrentSkipListSet<>(primeCompares.getPrimeFactorComparator());
		pfs.add(aPrime);
		newSets.add(pfs);
		superSet.addAll(newSets);
	}
	
	/**
	 * Lookup existing prime from set of all known primes or create new reference
	 * @param prime
	 * @return
	 */
	PrimeRefIntfc<E> findOrCreatePrimeRef(E targetPrimeVal, NavigableSet<PrimeRefIntfc<E>> onCreateBases)
	{
		PrimeRefIntfc<E> primeRef = null;
		Iterator<PrimeRefIntfc<E>> it = newPrimes.iterator();
		
		while (it.hasNext())
		{
			primeRef = it.next();
			E primeVal = primeRef.getPrime();
			if (targetPrimeVal.equals(primeVal))
			{
				newPrimes.remove(primeRef);
				return primeRef;
			}
			else if (primeVal.compareTo(targetPrimeVal) > 0) // with ordered set ; this implies not found
			{
				break;
			}
		}						
					
		return new PrimeRef<E>(targetPrimeVal, onCreateBases, primeCompares);
	}
	
	/**
	 * Predicate
	 * 
	 * Weed out sets which cannot represent the next prime.  Should avoid calling for bootstrap sets [], [,1], [1]
	 * 
	 * @param sumOfPrimeSet
	 * @return true for sum that is viable prime; false otherwise
	 */
	boolean viableSum(E sumOfPrimeSet, E lastMaxPrime)
	{
		boolean isPrimeSum = true;
		
		while (true)
		{
			// This was bootstrap logic while getting the general framework working.
			// This block should be removed and allow the remaining blocks to determine next prime.
			if (!Primes.isPrime(sumOfPrimeSet.intValue()))
			{
				isPrimeSum =  false;				
			}
			
			// Part of "non-cheating" prime checks.
			// only want sets summing to greater than the current
			// max prime.
			if (sumOfPrimeSet.compareTo(lastMaxPrime) <= 0)
			{
				isPrimeSum = false;
			}
			
			// Part of "non-cheating" prime checks.
			// Not a prime if is even. 
			if ((sumOfPrimeSet.longValue() & 1l) == 0)
			{
				isPrimeSum = false;
			}
			
			break;
		}
		
		return isPrimeSum;
	}
	
	/**
	 * Construct the sets representing the next possible prime and weed down to valid sets.
	 * @return
	 */
	public PrimeRefIntfc<E> nextPrimeRef()
	{	
		PrimeRefIntfc<E> aPrime = newPrimes.higher(lastMaxPrime);
		if (aPrime != null)
		{		
			logState("final candidate-1 -> ", aPrime);
			return addPrime(aPrime);
		}
				
		if (outputPrimes.size() > 30)
			return null;
				
		// This guava powerset implementation is limited to 30 items.  Replace with something that targets the general
		// criteria I expect to work.
		//
		// Represents all the initial possible sets to evaluate.
		E curMaxPrime = lastMaxPrime.getPrime();	
		Optional<PrimeRefIntfc<E>> primeRef = powerSet() // not quite powerset - use the outputPrimes set as input 
		.parallelStream()
		.filter(potentialSet -> potentialSet.stream().map(PrimeRefIntfc::getPrime).allMatch( prime -> prime.compareTo(curMaxPrime) <= 0))
		.filter(potentialSet -> viableSum((E) potentialSet.stream()
														.map(PrimeRefIntfc::getPrime)
														.map(l -> l.longValue())
														.reduce(0L, (prime1, prime2) -> (prime1 + prime2)), curMaxPrime))
		.map(potentialSet -> {
							E sumOfPrimes = 
									(E) potentialSet
							.stream()
							.map(PrimeRefIntfc::getPrime)
							.map(l -> l.longValue())
							.reduce(0L, (prime1, prime2) -> (prime1 + prime2));
							NavigableSet<PrimeRefIntfc<E>> setOfPrimes = new ConcurrentSkipListSet<>(primeCompares.getPrimeFactorComparator());
							setOfPrimes.addAll(potentialSet);
							PrimeRefIntfc<E> tmpPrimeRef = findOrCreatePrimeRef(sumOfPrimes, setOfPrimes);				
							newPrimes.add(tmpPrimeRef);
							return tmpPrimeRef;
						})
		.min(primeCompares.getPrimeFactorComparator());	
		
		logSet("all prime set: ", outputPrimes);
		logSet("derived prime set: ", newPrimes);
		
		newPrimes.stream().forEach(p -> updateSuperSet(p));
		
		if (primeRef.isPresent())
		{
			PrimeRefIntfc<E> nextPrime = primeRef.get();					
			logState("final candidate-2 ->", nextPrime);
			
			return addPrime(nextPrime);
		}
		return null;
	}
	
	/**
	 * Need to write a custom version of this since the Guava version is limiting - max of 30 values.
	 * @return
	 */
	Set<NavigableSet<PrimeRefIntfc<E>>> powerSet()
	{
		return superSet;
	}
	
	void logSet(String msg, Collection<PrimeRefIntfc<E>> p)
	{	
		String setStr = p.stream().map(PrimeRefIntfc::getPrime).map(l -> l.toString()).collect(Collectors.joining(","));
		
		String s = String.format("%s:    primes [%s]", msg, setStr);
		log.info(s);
	}
	
	void logState(String msg, PrimeRefIntfc<E> p)
	{	
		String s = String.format("%s:    primeRef [%d]", msg, p.getPrime());
		log.info(s);
	}	
}
