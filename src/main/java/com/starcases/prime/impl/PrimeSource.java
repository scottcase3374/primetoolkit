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
	
	// Primes output and primes generated
	private ArrayList<BigInteger> primes = new ArrayList<>();	
	private ArrayList<PrimeRefIntfc> primeRefs = new ArrayList<>();
	
	public PrimeSource()
	{
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
		BitSet potentialBases = new BitSet();	
		
		do 
		{		
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
			 *  Given that each new prime is based on the last set of primes - I'm 
			 */
			final int numBitsForPrimeCount = primeRefs.size();			 			
			final BigInteger maxSuperSetRow = BigInteger.ONE.shiftLeft(numBitsForPrimeCount);
			final BigInteger breakPointRow = BigInteger.ONE.shiftLeft(numBitsForPrimeCount-1);
			
			BigInteger superSetRow = maxSuperSetRow;
			BigInteger minSum = BigInteger.ZERO;
			while (superSetRow.compareTo(breakPointRow) > 0) 
			{	
				superSetRow = superSetRow.subtract(BigInteger.ONE);	
				
				BigInteger sum = BigInteger.ZERO;
				potentialBases.clear();
				for (int bitId = numBitsForPrimeCount-1; bitId >= 0  ; bitId--)
				{
					if (superSetRow.testBit(bitId))
					{						
						potentialBases.set(bitId);
						sum = sum.add(getPrime(bitId));
					}
				}
				
				if (minSum.equals(BigInteger.ZERO))
					minSum = sum.add(BigInteger.ONE);
				
				final int cachedCurIdx = nextIdx.get();
				final BigInteger curPri = getPrime(cachedCurIdx); 
				if (viablePrime(sum, curPri, minSum ))
				{					
					final BigInteger cachedSum = sum;
					final BitSet cachedBases = potentialBases.get(0,  numBitsForPrimeCount);
										
					insertNextMinimalPrime = (Boolean) -> addPrimeRef(cachedSum, cachedBases, cachedCurIdx, true);
					minSum = sum;
				}
			}
			
			insertNextMinimalPrime.accept(true);
			insertNextMinimalPrime = (Boolean b) -> b.hashCode(); // No-op
			
		} while (nextIdx.get() < 31 );
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
			
			log.info(String.format("addPrimeRef <new prime added> new-prime[%d] newIdx[%d] canAddBase[%b] base-indexes %s new-base-primes %s ", newPrime, nextIdx.get(), canAddBase, getIndexes(base), getPrimes(base)));
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
	boolean viablePrime(BigInteger primeSum, BigInteger lastMaxP, BigInteger minSum)
	{
		boolean isPrimeSum = false;
			
		do 
		{			
			// Part of "non-cheating" prime checks.
			// only want sets summing to greater than the current
			// max prime.
			if (primeSum.compareTo(lastMaxP) <= 0)
				break;
			
			// Part of "non-cheating" prime checks.
			// Not a prime if is even. 
			if (!primeSum.testBit(0))
				break;
			
			// This was bootstrap logic while getting the general framework working.
			// This block should be removed and allow the remaining blocks to determine next prime.
			if (!primeSum.isProbablePrime(1000))				
				break;
		
			if (primeSum.compareTo(minSum) > 0)
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
			int prevIdx = lastReturnedIdx;
			int idxFound = ++lastReturnedIdx;
			
			if (idxFound > 30 || idxFound > primesSize)
				break;
			
			PrimeRefIntfc existingPrime = getPrimeRef(idxFound);
			
			log.info(String.format("Prime [%d] PrimeIdx [%d]  prevMaxPrimeIdx[%d]", existingPrime.getPrime(), idxFound, prevIdx));
			return existingPrime;
		} 
		while(false);
				
		return null;
	}	
}
