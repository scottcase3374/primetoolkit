package com.starcases.newprime;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;
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
**/
@Getter
@Setter
@Log
@EqualsAndHashCode
public class PrimeRef implements PrimeIntfc, Comparable<PrimeRef>
{
	//
	// Shared functions
	//
	private static Comparator<PrimeRef> pfComparator = new Comparator<PrimeRef>() {
		@Override
		public int compare(PrimeRef o1, PrimeRef o2) {		
			return o1.compareTo(o2);
		}};
		
	//
	// Shared Data
	//
	
	// Primes encountered
	private static SortedSet<PrimeRef> allPrimes = new TreeSet<>(pfComparator);
	
	// Current max prime - seed with 0.
	private static long curMaxPrime = 0L;
	
	//
	// Instance data
	//
	
	// This instance of a prime
	long prime;
	
	// Represents the sets of base primes that sum to this prime.
	@EqualsAndHashCode.Exclude Set<Collection<PrimeRef>> primeBases = new TreeSet<>();
	
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
		SortedSet<PrimeRef> pfc = new TreeSet<>(pfComparator);
		pfc.add(this);
		addPrimeBase(pfc);
	}
	
	private PrimeRef(long prime, Collection<PrimeRef> bases)
	{
		this.prime = prime;
		addPrimeBase(bases);
	}
	
	/**
	 * Include a set of primes in the set of prime bases for the current prime.
	 * @param primeBase
	 */
	public void addPrimeBase(Collection<PrimeRef> primeBase)
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
	private static void addPrime(PrimeRef aPrime)
	{
		//logState("Add to all Primes", aPrime);
		allPrimes.add(aPrime);
	}	
	
	/**
	 * Lookup existing prime from set of all known primes
	 * @param prime
	 * @return
	 */
	public static Optional<PrimeRef> findPrimeRef(long prime)
	{
		PrimeRef t = new PrimeRef(prime);
		
		SortedSet<PrimeRef> tmp = allPrimes.subSet(t,t);
		return Optional.ofNullable(tmp.first());
		//return allPrimes.parallelStream().filter(p -> p.getPrime() == prime ).findFirst();
	}
	
	/**
	 * Predicate
	 * 
	 * Weed out sets which cannot represent the next prime.  Should avoid calling for bootstrap sets [], [,1], [1]
	 * 
	 * @param val
	 * @return true for sum that is viable prime; false otherwise
	 */
	private static boolean viableSum(long val)
	{
		boolean defRet = true;
		
		while (true)
		{
			// This was bootstrap logic while getting the general framework working.
			// This block should be removed and allow the remaining blocks to determine next prime.
			if (!Primes.isPrime((int)val))
			{
				defRet =  false;				
			}
			
			// Part of "non-cheating" prime checks.
			// only want sets summing to greater than the current
			// max prime.
			if (val <= curMaxPrime)
			{
				defRet = false;
			}
			
			// Part of "non-cheating" prime checks.
			// Not a prime if is even. 
			if ((val & 1l) == 0)
			{
				defRet = false;
			}
			
			break;
		}
		
		return defRet;
	}
	

	
	/**
	 * Construct the sets representing the next possible prime and weed down to valid sets.
	 * @return
	 */
	public static PrimeRef nextPrimeRef()
	{
		// Bootstrap logic 
		if (allPrimes.size() < 2)
		{
			PrimeRef bootStrap = findPrimeRef(++curMaxPrime).orElseGet(() -> new PrimeRef(curMaxPrime));
			addPrime(bootStrap);
			curMaxPrime = bootStrap.getPrime();
			return bootStrap;
		}
		
		Collection<PrimeRef> candidatePrimes = new TreeSet<>();
		
		// This guava powerset implementation is limited to 30 items.  Replace with something that targets the general
		// criteria I expect to work.
		//
		// Represents all the initial possible sets to evaluate.  
		powerSet(allPrimes)
		.filter(potentialPS -> potentialPS.size() > 1)
		.filter(potentialPS -> potentialPS.stream().map(PrimeRef::getPrime).allMatch( pl -> pl <=  curMaxPrime))
		.filter(potentialPS -> viableSum(potentialPS.stream().map(PrimeRef::getPrime).reduce(0L, (p1, p2) -> p1 + p2)))
		.forEach(potentialPS -> {
							long tmpCandidate = 
									potentialPS
									.stream()
									.map(PrimeRef::getPrime)
									.reduce(0L, (p1, p2) -> p1 + p2);
							
							PrimeRef nextPrimeRef = findPrimeRef(tmpCandidate).orElse(new PrimeRef(tmpCandidate, potentialPS));
							
												
							candidatePrimes.add(nextPrimeRef);
						});	
		
		Optional<PrimeRef> nextPrimeRef = candidatePrimes.stream().min(pfComparator);
		nextPrimeRef.ifPresent(PrimeRef::addPrime);
		nextPrimeRef.ifPresent(p -> curMaxPrime = p.getPrime());
		//logState("final candidate", nextPrimeRef.get());
		return nextPrimeRef.get();
	}
	
	/**
	 * Need to write a custom version of this since the Guava version is limiting - max of 30 values.
	 * @param p
	 * @return
	 */
	private static Stream<Set<PrimeRef>> powerSet(Collection<PrimeRef> p)
	{
		
		return Sets.powerSet(Sets.newLinkedHashSet(p)).stream();
	}
	
	/*
	private static void logStatePFSet(String msg, Set<PrimeRef> pfSet)
	{
		String s = String.format("msg: [%s] curMaxPrime[%d] sets[%s]", msg, curMaxPrime,
				pfSet
					.stream()
					.map(PrimeRef::getPrime)
					.map(ap -> ap.toString())
					.collect(Collectors.joining(",", "[", "]")));
		log.info(s);
	}
	
	private static void logState(String msg, PrimeRef p)
	{
		Set<Set<PrimeRef>> bases = p.getPrimeBases();
		
		String s = String.format("%s:    primeRef [%d]  baseVals[%s], baseSum[%s], curMaxP[%d]", 
				msg,
				p.getPrime(),
				
				bases.stream()
						.map(sp -> sp.stream()
								.map(PrimeRef::getPrime)
								.map(ap -> ap.toString())
								.collect(Collectors.joining(",", "[", "]"))
						)
						.collect(Collectors.joining(",")),
				
				bases.stream()
					.map(sp -> sp.stream()
								.map(PrimeRef::getPrime)
								.reduce(0L, (l1, l2) -> l1 + l2)
								.toString()
						)
					.collect(Collectors.joining(",")),
				curMaxPrime);
		log.info(s);
	}
*/
}
