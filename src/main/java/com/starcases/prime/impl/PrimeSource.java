package com.starcases.prime.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicBoolean;
import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import java.util.Iterator;
import lombok.extern.java.Log;

/**
 * Provides data structure holding the core data and objects that provide access to the data from the
 * "public interface for primes". Also some utility support to support alternative algs for
 * finding primes, working with bases of the primes, and general information/access.
 *
 */
@Log
public class PrimeSource implements PrimeSourceIntfc
{	
	private int confidenceLevel = 100;
	private AtomicInteger nextIdx = new AtomicInteger(-1);
	
	private List<BigInteger> primes;	
	private List<PrimeRefIntfc> primeRefs;
	private List<BigInteger> distanceToNext;
	
	private int targetPrimeCount;
	
	private BaseTypes activeBaseId = BaseTypes.DEFAULT;
	private AtomicBoolean doInit = new AtomicBoolean(false);
	
	//
	// initialization
	//
	
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
		
		var tmpBitSet = new BitSet();
		tmpBitSet.set(0);
		addPrimeRef(BigInteger.valueOf(1L), tmpBitSet.get(0, 1));
		
		tmpBitSet.clear();
		tmpBitSet.set(1);
		addPrimeRef(BigInteger.valueOf(2L), tmpBitSet.get(0, 2));
	}
	
	@Override
	public void init()
	{	
		if (doInit.compareAndExchangeAcquire(false, true))
		{
			return;
		}
		else
		{
			log.entering("PrimeSource", "init()");
		}
		
		
		
		final var primeIndexMaxPermutation = new BitSet();
		var primeIndexPermutation = new BitSet();
		
		// each iteration increases the #bits by 1; i.e. a new prime is determined per iteration
		do 
		{
			final var curPrimeIdx = nextIdx.get();
			final var curPrime = getPrime(curPrimeIdx);
			
			// Represents a X-bit search space of indexes for primes to add for next prime.
			final var numBitsForPrimeCount = primeRefs.size()-1;
			primeIndexMaxPermutation.clear(numBitsForPrimeCount-1);
			primeIndexMaxPermutation.set(numBitsForPrimeCount); // keep 'shifting' max bit left
			
			// no reason to perform work when no indexes 
			// selected so start with 1 index selected.
			primeIndexPermutation.clear();
			primeIndexPermutation.set(0); 
			
			BigInteger sumCeiling = curPrime.stream().map(this::calcSumCeiling).reduce(BigInteger.ZERO, BigInteger::add);
			
			while (!primeIndexPermutation.equals(primeIndexMaxPermutation)) 
			{					
				var permutationSum = primeIndexPermutation
						.stream()						
						.mapToObj(this::getPrime)
						.filter(Optional::isPresent)
						.map(Optional::get)
						.reduce(curPrime.get(), BigInteger::add);
					
				if (permutationSum.compareTo(sumCeiling) > 0)
				{
					// limit useless work - if we exceed a known prime then we are
					// done with this iteration.
					break;
				}
				
				if (curPrime.isPresent() && viablePrime(permutationSum, curPrime.get()))
				{
					final var cachedSum = permutationSum;										
					var sumBaseIdxs = primeIndexPermutation.get(0, numBitsForPrimeCount);
					sumBaseIdxs.set(curPrimeIdx); // sum of primes from these indexes should match 'sum'
					final var cachedBases = sumBaseIdxs;
					addPrimeRef(cachedSum, cachedBases, curPrimeIdx, true);
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
	}
	
	//
	// non-navigation related / base selection
	//
	
	@Override
	public BaseTypes getActiveBaseId()
	{
		return activeBaseId;
	}
	
	@Override
	public void setActiveBaseId(BaseTypes activeBaseId)
	{
		this.activeBaseId = activeBaseId;	
	}
	
	@Override
	public boolean baseExist(BaseTypes baseId)
	{
		boolean ret = false;
		try
		{
			primeRefs.get(0).getMinPrimeBase(baseId);
			ret = true;
		}
		catch(Exception e)
		{
			// base doesn't exist
		}
		return ret;
	}
	//
	// navigation - non-index based api
	//
	 
	@Override
	public Iterator<PrimeRefIntfc> getPrimeRefIter()
	{
		return primeRefs.iterator();
	}
	
	//
	// search and retrieve - non-index apis
	//

	@Override
	public Optional<PrimeRefIntfc> getPrimeRef(BigInteger val)
	{
		var idx = getPrimeIdx(val);
		if (idx < 0 || idx > this.getMaxIdx())
			return Optional.empty();
		return getPrimeRef(idx);
	}

	@Override
	public Optional<PrimeRefIntfc> getNearPrimeRef(BigInteger val)
	{
		log.info(String.format("get near prime ref bigint %d", val));
		var dir = val.signum() < 0 ? -2 : -1;
		
		var ret = Collections.binarySearch(primes, val.abs());

		if (ret < 0) // not found but have 'insertion idx' which is negative
		{
			ret = -ret + dir;  // fixup insertion idx  and adjust for searching downward/upward
		}

		if (ret > getMaxIdx())
			 ret = getMaxIdx(); // if fixed up idx location past end then use end
		
		var r = getPrimeRef(ret); 
		log.info( String.format("get near primeref big int [%d] return [%s]", val, r.isPresent() ? r.get().getPrime().toString() : "n/a"));
		return r;  	 	
	}

	@Override
	public Optional<PrimeRefIntfc> getNearPrimeRef(BigDecimal val)
	{	
		var tmpVal = val.setScale(0, RoundingMode.CEILING).toBigInteger();
		
		return getNearPrimeRef(tmpVal);  	
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefWithinOffset(BigInteger val, BigInteger maxPrimeOffset)
	{		
		var dir = maxPrimeOffset.signum() < 0 ? -2 : -1;
		
		var ret = Collections.binarySearch(primes, val.abs());

		if (ret < 0) // not found but have 'insertion idx' which is negative
		{
			ret = -ret + dir;  // fixup insertion idx  and adjust for searching downward/upward	
		}

		if (ret > getMaxIdx())
			 ret = getMaxIdx(); // if fixed up idx location past end then use end

		var r = getPrimeRef(ret);
		
		if (r.get().getPrime().compareTo(val.add(maxPrimeOffset)) > 1)
			r = this.getNearPrimeRef(r.get().getPrime().subtract(BigInteger.ONE));
		
		log.info( String.format("get near primeref big int [%d] return [%s]", val, r.isPresent() ? r.get().getPrime().toString() : "n/a"));
		return r;  	 	
	}
	
	@Override
	public Optional<PrimeRefIntfc> getPrimeRefWithinOffset(BigDecimal val, BigDecimal maxPrimeOffset)
	{
		return getPrimeRefWithinOffset(val.toBigInteger(), maxPrimeOffset.toBigInteger());
	}
	
	@Override
	public Optional<PrimeRefIntfc> getPrimeRef(BigInteger val, BigInteger exactOffset)
	{
		return getPrimeRef(val.add(exactOffset));
	}
	
	public Optional<BigInteger> getDistToNextPrime(PrimeRefIntfc prime)
	{
		return prime.getDistToNextPrime();
	}

	public Optional<BigInteger> getDistToPrevPrime(PrimeRefIntfc prime)
	{
		return prime.getDistToPrevPrime();
	}
	
	//
	// navigation - index based apis
	//
	
	
	@Override
	public BigInteger getDistToPrevPrime(int curIdx)
	{
		return  this.distanceToNext.get(curIdx-1).negate();
	}
	
	/**
	 * Diff of primes at the 2 indexes; order of indexes affects whether result is +/-.
	 * @param idx1
	 * @param idx2
	 * @return
	 */
	public Optional<BigInteger> getDistBetween(int idx1, int idx2)
	{
		if (idx1 < 0 || idx2 < 0 || idx1 > getMaxIdx() || idx2 > getMaxIdx() )
			return Optional.empty();
		
		return  Optional.of(  idx1 < idx2 ? this.getPrime(idx2).get().subtract(this.getPrime(idx1).get()) : this.getPrime(idx1).get().subtract(this.getPrime(idx2).get()));
	}
	
	@Override
	public int getPrimeIdx(BigInteger val)
	{
		return this.primes.indexOf(val);
	}

	@Override
	public Optional<BigInteger> getPrime(int primeIdx)
	{
		if (primeIdx > getMaxIdx() || primeIdx < 0)
			return Optional.empty();
		
		return Optional.of(primes.get(primeIdx));
	}
	
	@Override
	public Optional<PrimeRefIntfc> getPrimeRef(int primeIdx)
	{
		if (primeIdx > getMaxIdx() || primeIdx < 0)
			return Optional.empty();

		return   Optional.of(primeRefs.get(primeIdx));		
	}

	@Override
	public BigInteger getDistToNextPrime(int curIdx)
	{
		return this.distanceToNext.get(curIdx);
	}
	
	public long getNextLowPrime(BigInteger val, int startIdx, int maxOffset)
	{
		var ret = Collections.binarySearch(primes, val);
		if (ret < startIdx || ret >= maxOffset)
			return -1;
		return (long)ret-1;  
	}
	
	public long getNextHighPrime(BigInteger val, int startIdx, int maxOffset)
	{
		var ret = Collections.binarySearch(primes, val);
		if (ret < startIdx || ret >= maxOffset)
			return -1L;
		return  (long)ret+1;  
	}		
	
	/**
	 * return value matches java binarySearch() return foundidx-1 for any result > 0; otherwise returns -val
	 * @param val
	 * @return
	 */
	@Override
	public int getNextLowPrimeIdx(BigInteger val)
	{
		var ret = Collections.binarySearch(primes, val);
		return ret >= 0 ? Math.max(ret-1, 0) : (-ret-2);  
	}
		
	@Override
	public int getNextHighPrimeIdx(BigInteger val)
	{
		var ret = Collections.binarySearch(primes, val);
		return ret >= 0 ? ret+1 : Math.max(-ret-1, 0);  
	}

	@Override
	public int getNextLowPrimeIdx(BigDecimal val)
	{
		return getNextLowPrimeIdx(val.setScale(0, RoundingMode.CEILING).toBigInteger()); 
	}

	@Override
	public int getNextHighPrimeIdx(BigDecimal val)
	{
		return getNextHighPrimeIdx(val.setScale(0, RoundingMode.CEILING).toBigInteger());  
	}
	
	@Override
	public int getMaxIdx()
	{
		return primeRefs.size()-1;
	}
	
	//
	// Private apis
	//
	
	private BigInteger calcSumCeiling(BigInteger primeSum)
	{
		return primeSum.shiftLeft(1).subtract(BigInteger.ONE);
	}
	
	private void incrementPermutation(BitSet primePermutation)
	{
		var bit = 0;
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
	 * 
	 * @param aPrime
	 */
	private void addPrimeRef(BigInteger newPrime, BitSet base, int curPrimeIdx, boolean canAddBase)
	{
		if (canAddBase && newPrime.equals(getPrime(curPrimeIdx).get()))
		{			
			var p = getPrimeRef(curPrimeIdx); 						
			p.ifPresent(pr -> pr.addPrimeBase(base));
			log.info(String.format("addPrimeRef <added base> new-prime[%d] new-base-indexes %s new-base-primes %s   cur-Prime[%d]", newPrime, getIndexes(base),getPrimes(base), getPrime(curPrimeIdx).get()));					
		}
		else
		{					
			var idx = nextIdx.incrementAndGet();
						
			primes.add(idx, newPrime);
			PrimeRefIntfc ret = new PrimeRef(idx, base);
			primeRefs.add(idx, ret);	
			
			distanceToNext.add(null);
			getPrime(curPrimeIdx).ifPresent(p -> distanceToNext.set(curPrimeIdx, newPrime.subtract(p)));
		}
	}	
		
	public boolean distinct(PrimeRefIntfc [] vals)
	{
		return Arrays.asList(vals).stream().distinct().count() == vals.length;
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
		var isPrimeSum = false;
			
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
		return bs.stream().boxed().map(Object::toString).collect(Collectors.joining(",","[", "]"));
	}

	private String getPrimes(BitSet bs)
	{
		return bs.stream().boxed().map(i -> this.getPrime(i).toString()).collect(Collectors.joining(",","[", "]"));
	}
}
