package com.starcases.newprime;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.math3.primes.Primes;
import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

/**
* General algorithm uses powerset of processed primes as source of potential base sets for next prime. 
* The general idea being that any future prime should be representable through the sum of a subset of the
* previous primes.
* 
* As as second phase of this; after seeing how many primes are found in a single pass but only 1 retained;
* looking to retain/return those primes without additional processing.
**/
@Getter
@Setter
@Log
@EqualsAndHashCode
public class PrimeRef implements Comparable<PrimeRef>
{
	//
	// Shared functions
	//
	private static Comparator<PrimeRef> pfComparator = (PrimeRef o1, PrimeRef o2) -> o1.compareTo(o2); 
	
	private static Comparator<NavigableSet<PrimeRef>> ssComparator = 
			(NavigableSet<PrimeRef> ss1, NavigableSet<PrimeRef> ss2) 
				-> {
						Iterator<Long> s2 = ss2.stream().map(PrimeRef::getPrime).iterator();						
						return ss1.stream().map(PrimeRef::getPrime).map(l -> l - s2.next()).filter(x -> x == 0).findFirst().orElse(0L).intValue();					
					};
	//
	// Shared Data
	//
	
	// Primes encountered
	private static NavigableSet<PrimeRef> outputPrimes = new ConcurrentSkipListSet<>(pfComparator);
	
	private static NavigableSet<PrimeRef> newPrimes = new ConcurrentSkipListSet<>(pfComparator);
	private static AtomicInteger numAllPrimes = new AtomicInteger(0);
	
	private static PrimeRef pf0 = new PrimeRef(0L);
	private static PrimeRef pf1 = new PrimeRef(1L);
	private static PrimeRef pf2 = new PrimeRef(2L);
	private static PrimeRef lastMaxPrime = pf0;
	
	static 
	{
		// Bootstrap
		newPrimes.add(pf1);
		newPrimes.add(pf2);
	}
	//
	// Instance data
	//
	
	// This instance of a prime
	long prime;
	
	// Represents the sets of base primes that sum to this prime.
	@EqualsAndHashCode.Exclude Set<NavigableSet<PrimeRef>> primeBases = new ConcurrentSkipListSet<>(ssComparator);
	
	// 
	// Instance data maint
	//
	
	/**
	 * Handle simple prime where the base is simply itself - i.e. 1, 2 
	 * Simplifies bootstrapping
	 * 
	 * @param prime
	 */
	private PrimeRef(long prime)
	{
		this.prime = prime;
		NavigableSet<PrimeRef> pfc = new ConcurrentSkipListSet<>(pfComparator);
		pfc.add(this);
		addPrimeBase(pfc);
	} 
	
	private PrimeRef(long prime, NavigableSet<PrimeRef> bases)
	{
		this.prime = prime;
		addPrimeBase(bases);
	}
	
	/**
	 * Include a set of primes in the set of prime bases for the current prime.
	 * @param primeBase
	 */
	public void addPrimeBase(NavigableSet<PrimeRef> primeBase)
	{
		primeBases.add(primeBase);
	}
	
	/**
	 * Override implements Comparable<PrimeRef>
	 * Natural order based on the Longs used to represent the primes.
	 */
	@Override
	public int compareTo(PrimeRef o) 
	{	
		return (int)(this.getPrime() - o.getPrime());
	}		
	
	//
	// Static/shared data maint
	//
	
	/**
	 * Add new prime to shared set of all primes
	 * @param aPrime
	 */
	private static PrimeRef addPrime(PrimeRef aPrime)
	{
		numAllPrimes.getAndIncrement();
		outputPrimes.add(aPrime);
		newPrimes.remove(aPrime);
		lastMaxPrime = aPrime;	
		return lastMaxPrime;
	}	
	
	/**
	 * Lookup existing prime from set of all known primes or create new reference
	 * @param prime
	 * @return
	 */
	public static PrimeRef findOrCreatePrimeRef(long targetPrimeVal, NavigableSet<PrimeRef> onCreateBases)
	{
		PrimeRef primeRef = null;
		Iterator<PrimeRef> it = newPrimes.iterator();
		
		while (it.hasNext())
		{
			primeRef = it.next();
			long primeVal = primeRef.getPrime();
			if (primeVal == targetPrimeVal)
			{
				newPrimes.remove(primeRef);
				return primeRef;
			}
			else if (primeVal > targetPrimeVal) // with ordered set ; this implies not found
			{
				break;
			}
		}						
					
		return new PrimeRef(targetPrimeVal, onCreateBases);
	}
	
	/**
	 * Predicate
	 * 
	 * Weed out sets which cannot represent the next prime.  Should avoid calling for bootstrap sets [], [,1], [1]
	 * 
	 * @param sumOfPrimeSet
	 * @return true for sum that is viable prime; false otherwise
	 */
	private static boolean viableSum(long sumOfPrimeSet, long lastMaxPrime)
	{
		boolean isPrimeSum = true;
		
		while (true)
		{
			// This was bootstrap logic while getting the general framework working.
			// This block should be removed and allow the remaining blocks to determine next prime.
			if (!Primes.isPrime((int)sumOfPrimeSet))
			{
				isPrimeSum =  false;				
			}
			
			// Part of "non-cheating" prime checks.
			// only want sets summing to greater than the current
			// max prime.
			if (sumOfPrimeSet <= lastMaxPrime)
			{
				isPrimeSum = false;
			}
			
			// Part of "non-cheating" prime checks.
			// Not a prime if is even. 
			if ((sumOfPrimeSet & 1l) == 0)
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
	public static PrimeRef nextPrimeRef()
	{		
		PrimeRef aPrime = newPrimes.higher(lastMaxPrime);
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
		long curMaxPrime = lastMaxPrime.getPrime();		
		Optional<PrimeRef> primeRef = powerSet(outputPrimes)
		.parallelStream()
		.filter(potentialSet -> potentialSet.stream().map(PrimeRef::getPrime).allMatch( prime -> prime <=  curMaxPrime))
		.filter(potentialSet -> viableSum(potentialSet.stream()
														.map(PrimeRef::getPrime)
														.reduce(0L, (prime1, prime2) -> prime1 + prime2), curMaxPrime))
		.map(potentialSet -> {
							long sumOfPrimes = 
									potentialSet
									.stream()
									.map(PrimeRef::getPrime)
									.reduce(0L, (prime1, prime2) -> prime1 + prime2);
							NavigableSet<PrimeRef> setOfPrimes = new ConcurrentSkipListSet<>(pfComparator);
							setOfPrimes.addAll(potentialSet);
							PrimeRef tmpPrimeRef = findOrCreatePrimeRef(sumOfPrimes, setOfPrimes);				
							newPrimes.add(tmpPrimeRef);
							return tmpPrimeRef;
						})
		.min(pfComparator);	
		
		logSet("all prime set: ", outputPrimes);
		logSet("derived prime set: ", newPrimes);
		
		if (primeRef.isPresent())
		{
			PrimeRef nextPrime = primeRef.get();					
			logState("final candidate-2 ->", nextPrime);
			return addPrime(nextPrime);
		}
		return null;
	}
	
	/**
	 * Need to write a custom version of this since the Guava version is limiting - max of 30 values.
	 * @param p
	 * @return
	 */
	private static Set<Set<PrimeRef>> powerSet(NavigableSet<PrimeRef> p)
	{
		return Sets.powerSet(p);
	}

	private static void logSet(String msg, Set<PrimeRef> p)
	{	
		String setStr = p.stream().map(PrimeRef::getPrime).map(l -> l.toString()).collect(Collectors.joining(","));
		
		String s = String.format("%s:    primes [%s]", msg, setStr);
		log.info(s);
	}
	
	private static void logState(String msg, PrimeRef p)
	{	
		String s = String.format("%s:    primeRef [%d]", msg, p.getPrime());
		log.info(s);
	}
}
