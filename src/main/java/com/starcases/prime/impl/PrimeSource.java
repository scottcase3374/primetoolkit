package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;

@Log
public class PrimeSource implements PrimeSourceIntfc
{
	// for output
	private int lastReturnedIdx = -1; 
	
	private AtomicInteger nextIdx = new AtomicInteger(-1);
	
	private ArrayList<BigInteger> primes;	
	private ArrayList<PrimeRefIntfc> primeRefs;
	
	private int maxCount;
	
	public PrimeSource(int maxCount)
	{
		primes = new ArrayList<>(maxCount);
		primeRefs = new ArrayList<>(maxCount);
		this.maxCount = maxCount;
		
		PrimeRef.setPrimeSource(this);
		
		BitSet tmpBitSet = new BitSet();
		tmpBitSet.set(0);
		addPrimeRef(BigInteger.valueOf(1L), tmpBitSet.get(0, 1));
		tmpBitSet.clear();
		tmpBitSet.set(1);
		addPrimeRef(BigInteger.valueOf(2L), tmpBitSet.get(0, 2));
		
		init();
	}
	
	void init()
	{	
		BitSet sumBases = new BitSet(32);
		
		// Help Identify when required range of primes for bases is too small to allow
		// construction of the next prime.
		int curPrimeIdx;
		
		// Metric info 
		int maxBaseSize = 0;
		BigInteger sumWithMaxBase = BigInteger.ZERO;
		
		do 
		{
			curPrimeIdx = nextIdx.get();
			
			Consumer<Boolean> insertNextMinimalPrime = (Boolean b) -> b.hashCode(); // No-op
			/**
			 * Given a sequential list of primes, map the index within the list
			 * directly to a bit-number. The next prime will be the sum of a
			 * subset of the existing primes. The overall domain isn't a full
			 * powerset since the next prime wouldn't contain a 'null' type item or
			 * subsets that can't represent the next prime due to summing to a
			 * smaller value than the last max prime.
			 * 
			 * The 'superset' is a 'virtual' representation of the sets described above.
			 * It is virtual in the sense that the BigInteger is used more for representing
			 * combinations of bit-indexes of primes. 
			 * 
			 *  Given n primes, an integer of 2^n is the maximum number of combinations
			 *  of them. So the bit-value of the range 0 to 2^n is mappable to each combination
			 *  of the primes (where bit-id represents the index of the actual prime in a list). 
			 *  
			 *  Given that each new prime is based on the last set of primes - many primes
			 *  are constructable from the previous prime and a few smaller primes.
			 */
			
			// Represents a X-bit search space of indexes for primes to add for next prime.
			final int numBitsForPrimeCount = Math.min(primeRefs.size(), 20); 			 			
			final BigInteger allIndexBitsTrue = BigInteger.ONE.shiftLeft(numBitsForPrimeCount);
			
			BigInteger sumCeiling = BigInteger.ZERO;
			final BigInteger curPrime = getPrime(nextIdx.get());
			
			BigInteger primeIndexPerBit = BigInteger.ZERO;
			// Exit/complete the while loop after handling the next prime.
			while (primeIndexPerBit.compareTo(allIndexBitsTrue) < 0) 
			{	
				// Generate next permutation of the bit indexes
				primeIndexPerBit = primeIndexPerBit.add(BigInteger.ONE);
				
				BigInteger sum = curPrime; // start at current prime
				sumBases.clear();
				
				sumCeiling = sum.shiftLeft(1);
				
				// Represent a "small set of small primes" - primes from 1 bit-length 
				// to some 'small' # of bits. Full execution of
				for (int bitId = 0; bitId < numBitsForPrimeCount; bitId++)
				{
					if (primeIndexPerBit.testBit(bitId))
					{						
						sumBases.set(bitId);
						sum = sum.add(getPrime(bitId));
					}
				}
				
				if (viablePrime(sum, curPrime, sumCeiling))
				{
					final BigInteger cachedSum = sum;					
					sumBases.set(nextIdx.get()); // sum of primes from these indexes should match 'sum'
					final BitSet cachedBases = sumBases.get(0,  primeRefs.size());
										
					insertNextMinimalPrime = (Boolean) -> addPrimeRef(cachedSum, cachedBases, nextIdx.get(), true);
					sumCeiling = sum;
					
					// Metric info
					maxBaseSize = Math.max(maxBaseSize, sumBases.cardinality());
					if (maxBaseSize == sumBases.cardinality())
						sumWithMaxBase = sum;
				}
			}
			
			insertNextMinimalPrime.accept(true);
			insertNextMinimalPrime = (Boolean b) -> b.hashCode(); // No-op
			
			if (nextIdx.get() % 5 == 0)
				System.out.print("K");
			
			// identify when we didn't find a new prime which implies we didn't allow a large enough
			// set of "small primes" to use as the base.
			if (curPrimeIdx == nextIdx.get())
				break;
			
		} while (nextIdx.get() < maxCount);
		
		log.info(String.format("last new-prime[%d] newIdx[%d] base-indexes %s new-base-primes %s  max-base-size[%d] sum-with-max-base-size[%d]", 
				getPrime(nextIdx.get()), 
				nextIdx.get(),  
				getPrimeRef(nextIdx.get()).getIndexes(), 
				getPrimeRef(nextIdx.get()).getIdxPrimes(),
				maxBaseSize,
				sumWithMaxBase
				));		
	}
	
	/**
	 * Add new prime to shared set of all primes
	 * Base will be same as aPrime and using same index. 
	 * @param aPrime
	 */
	void addPrimeRef(BigInteger nextPrime, BitSet baseIdx)
	{
		addPrimeRef(nextPrime, baseIdx, 0, false);
	}
		
	/**
	 * Add new prime to shared set of all primes
	 * @param aPrime
	 */
	void addPrimeRef(BigInteger newPrime, BitSet base, int curPrimeIdx, boolean canAddBase)
	{
		if (canAddBase && newPrime.equals(getPrime(curPrimeIdx)))
		{			
			getPrimeRef(curPrimeIdx).addPrimeBase(base);
			log.info(String.format("addPrimeRef <added base> new-prime[%d] new-base-indexes %s new-base-primes %s   cur-Prime[%d]", newPrime, getIndexes(base),getPrimes(base), getPrime(curPrimeIdx)));
		}
		else
		{				
			int idx = nextIdx.incrementAndGet();						
			primes.add(idx, newPrime);
			PrimeRefIntfc ret = new PrimeRef(idx, base);
			primeRefs.add(idx, ret);			
			
			//log.info(String.format("addPrimeRef <new prime added> new-prime[%d] newIdx[%d] canAddBase[%b] base-indexes %s new-base-primes %s ", newPrime, nextIdx.get(), canAddBase, getIndexes(base), getPrimes(base)));
		}		
	}	
	
	public BigInteger getPrime(int primeIdx)
	{
		return primes.get(primeIdx);
	}
	
	public PrimeRefIntfc getPrimeRef(int primeIdx)
	{
		return primeRefs.get(primeIdx);		
	}
		
	/**
	 * 
	 * Weed out sums which cannot represent the next prime.								
	 * 
	 * @param sumOfPrimeSet
	 * @return true for sum that is viable prime; false otherwise
	 */
	boolean viablePrime(BigInteger primeSum, BigInteger lastMaxPrime, BigInteger sumCeiling)
	{
		boolean isPrimeSum = false;
			
		do 
		{			
			// Part of "non-cheating" prime checks.
			// only want sets summing to greater than the current
			// max prime.
			if (primeSum.compareTo(lastMaxPrime) <= 0)
				break;
			
			// Part of "non-cheating" prime checks.
			// Not a prime if is even. 
			if (!primeSum.testBit(0))
				break;
			
			// This was bootstrap logic while getting the general framework working.
			// This block should be removed and allow the remaining blocks to determine next prime.
			if (!primeSum.isProbablePrime(1000))				
				break;
		
			if (primeSum.compareTo(sumCeiling) > 0)
				break;
			
			isPrimeSum =  true;			
		} 
		while (false);
		
		return isPrimeSum;
	}
	
	String getIndexes(BitSet bs)
	{
		return bs.stream().boxed().map(i -> i.toString()).collect(Collectors.joining(",","[", "]"));
	}

	String getPrimes(BitSet bs)
	{
		return bs.stream().boxed().map(i -> this.getPrime(i).toString()).collect(Collectors.joining(",","[", "]"));
	}

	
	/**
	 * 
	 * @return
	 */
	public PrimeRefIntfc nextPrimeRef()
	{	
		int primesSize = primes.size();
		do
		{
			int idxFound = ++lastReturnedIdx;
			
			if (idxFound >= primesSize)
				break;
			
			PrimeRefIntfc existingPrime = getPrimeRef(idxFound);
			
			//log.info(String.format("Prime [%d] PrimeIdx [%d]  prevMaxPrimeIdx[%d]", existingPrime.getPrime(), idxFound, prevIdx));
			return existingPrime;
		} 
		while(false);
				
		return null;
	}	
}
