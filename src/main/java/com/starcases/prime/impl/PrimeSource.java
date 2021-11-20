package com.starcases.prime.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;

@Log
public class PrimeSource implements PrimeSourceIntfc
{
	private int confidenceLevel = 100;
	private AtomicInteger nextIdx = new AtomicInteger(-1);
	
	private List<BigInteger> primes;	
	private List<PrimeRefIntfc> primeRefs;
	private List<BigInteger> distanceToNext;
	
	private int targetPrimeCount;
	
	private int activeBaseId = 0;
	
	public PrimeSource(int maxCount, int confidenceLevel)
	{
		this(maxCount);
		this.confidenceLevel = confidenceLevel;	
	}
	
	public PrimeSource(int maxCount)
	{
		primes = new ArrayList<>(maxCount);
		primeRefs = new ArrayList<>(maxCount);
		distanceToNext = new ArrayList<>(maxCount);
		this.targetPrimeCount = maxCount;
		
		PrimeRef.setPrimeSource(this);
		
		BitSet tmpBitSet = new BitSet();
		tmpBitSet.set(0);
		addPrimeRef(BigInteger.valueOf(1L), tmpBitSet.get(0, 1));
		
		tmpBitSet.clear();
		tmpBitSet.set(1);
		addPrimeRef(BigInteger.valueOf(2L), tmpBitSet.get(0, 2));
	}
	
	public int getPrimeIdx(BigInteger val)
	{
		return this.primes.indexOf(val);
	}
	
	public BigInteger getDistToNextPrime(int curIdx)
	{
		return this.distanceToNext.get(curIdx);
	}
	
	public int getActiveBaseId()
	{
		return activeBaseId;
	}
	
	public void setActiveBaseId(int activeBaseId)
	{
		this.activeBaseId = activeBaseId;	
	}
	
	public long getNextLowPrime(BigInteger val, int startIdx, int maxOffset)
	{
		int ret = Collections.binarySearch(primes, val);
		if (Math.abs(startIdx - ret) >= maxOffset)
			return Long.MIN_VALUE;
		return ret > 0 ? ret-1 : (-ret)-1;  
	}
	
	public long getNextHighPrime(BigInteger val, int startIdx, int maxOffset)
	{
		int ret = Collections.binarySearch(primes, val);
		if (Math.abs(startIdx - ret) >= maxOffset)
			return Long.MIN_VALUE;
		return ret > 0 ? ret+1 : (-ret)+1;  
	}	
	
	/**
	 * return value matches java binarySearch() return foundidx-1 for any result > 0; otherwise returns -val
	 * @param val
	 * @return
	 */
	public int getNextLowPrime(BigInteger val)
	{
		int ret = Collections.binarySearch(primes, val);
		
		return ret > 0 ? ret-1 : (-ret)-1;  
	}

	public int getNextHighPrime(BigInteger val)
	{
		int ret = Collections.binarySearch(primes, val);
		
		return ret > 0 ? ret+1 : (-ret)+1;  
	}

	public int getMaxIdx()
	{
		return primeRefs.size()-1;
	}
	
	public void init()
	{	
		BigInteger sumCeiling; 
		
		final BitSet primeIndexMaxPermutation = new BitSet();
		BitSet primeIndexPermutation = new BitSet();
		
		// Metric info 
		int maxBaseSize = 0;
		BigInteger sumWithMaxBase = BigInteger.ZERO;
		
		// each iteration increases the #bits by 1; i.e. a new prime is determined per iteration
		do 
		{
			final int curPrimeIdx = nextIdx.get();
			final BigInteger curPrime = getPrime(curPrimeIdx);
			
			// Represents a X-bit search space of indexes for primes to add for next prime.
			final int numBitsForPrimeCount = primeRefs.size()-1;
			primeIndexMaxPermutation.clear(numBitsForPrimeCount-1);
			primeIndexMaxPermutation.set(numBitsForPrimeCount); // keep 'shifting' max bit left
			
			// no reason to perform work when no indexes 
			// selected so start with 1 index selected.
			primeIndexPermutation.clear();
			primeIndexPermutation.set(0); 
			
			sumCeiling = calcSumCeiling(curPrime);
			while (!primeIndexPermutation.equals(primeIndexMaxPermutation)) 
			{					
				BigInteger permutationSum = primeIndexPermutation
						.stream()						
						.mapToObj(this::getPrime)
						.collect(Collectors.reducing(curPrime, (a,b) -> a.add(b)));
					
				if (permutationSum.compareTo(sumCeiling) > 0)
				{
					// limit useless work - if we exceed a known prime then we are
					// done with this iteration.
					break;
				}
				
				if (viablePrime(permutationSum, curPrime))
				{
					final BigInteger cachedSum = permutationSum;										
					BitSet sumBaseIdxs = primeIndexPermutation.get(0, numBitsForPrimeCount);
					sumBaseIdxs.set(curPrimeIdx); // sum of primes from these indexes should match 'sum'
					final BitSet cachedBases = sumBaseIdxs;
						
					//log.info("cur prime:" + curPrime + " nextPrime:" + cachedSum);
					addPrimeRef(cachedSum, cachedBases, curPrimeIdx, true);
										
					// Metric info
					maxBaseSize = Math.max(maxBaseSize, sumBaseIdxs.cardinality());
					if (maxBaseSize == sumBaseIdxs.cardinality())
						sumWithMaxBase = permutationSum;
					
					break;
				}
				else
					incrementPermutation(primeIndexPermutation);
			}
			
			if (nextIdx.get() % 100000 == 0)
				log.info("100k");
			else if (nextIdx.get() % 1000 == 0)
				System.out.print("K");
		} 
		while (nextIdx.get() < targetPrimeCount);
		
		log.info(String.format("last new-prime[%d] newIdx[%d] base-indexes %s new-base-primes %s  max-base-size[%d] sum-with-max-base-size[%d]", 
				getPrime(nextIdx.get()), 
				nextIdx.get(),  
				getPrimeRef(nextIdx.get()).getIndexes(), 
				getPrimeRef(nextIdx.get()).getIdxPrimes(),
				maxBaseSize,
				sumWithMaxBase
				));		
	}
	
	private BigInteger calcSumCeiling(BigInteger primeSum)
	{
		return primeSum.shiftLeft(1).subtract(BigInteger.ONE);
	}
	
	private void incrementPermutation(BitSet primePermutation)
	{
		int bit = 0;
		// Generate next permutation of the bit indexes
		do	// performs add and carry if needed
		{
			primePermutation.flip(bit);
			
			if (primePermutation.get(bit)) // true means no carry
				break;
			
			bit++;	
		} while(true);					
	}
	
	/**
	 * Add new prime to shared set of all primes
	 * Base will be same as aPrime and using same index. 
	 * @param aPrime
	 */
	private void addPrimeRef(BigInteger nextPrime, BitSet baseIdx)
	{
		addPrimeRef(nextPrime, baseIdx, nextIdx.get()+1, false);
	}
		
	/**
	 * Add new prime to shared set of all primes
	 * @param aPrime
	 */
	private void addPrimeRef(BigInteger newPrime, BitSet base, int curPrimeIdx, boolean canAddBase)
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
			
			distanceToNext.add(null);
			BigInteger dist = curPrimeIdx > 0 ? newPrime.subtract(this.getPrime(curPrimeIdx)) : BigInteger.ONE;
			distanceToNext.set(curPrimeIdx, dist);			
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
	private boolean viablePrime(BigInteger primeSum, BigInteger lastMaxPrime)
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
			if (!primeSum.isProbablePrime(confidenceLevel))				
				break;
				
			isPrimeSum =  true;			
		} 
		while (false);
		
		return isPrimeSum;
	}
	
	private String getIndexes(BitSet bs)
	{
		return bs.stream().boxed().map(i -> i.toString()).collect(Collectors.joining(",","[", "]"));
	}

	private String getPrimes(BitSet bs)
	{
		return bs.stream().boxed().map(i -> this.getPrime(i).toString()).collect(Collectors.joining(",","[", "]"));
	}
}
