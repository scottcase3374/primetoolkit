package com.starcases.prime.graph.impl;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.stream.Collectors;
import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;

@Log
public class BaseReduce3Triple extends PrimeGrapher 
{
	static Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));

	static final int top = 0;
	static final int mid = 1;
	static final int bot = 2;
	
	public BaseReduce3Triple(PrimeSourceIntfc ps)
	{
		super(ps, log);
	}
	
	/*
	 *  given prime-idx, bitset
	 *  
	 *  find new bitset where bitset.size()-1 is reduced to the sum of
	 *  3 pre-existing primes. For example.
	 *     P     B
	 *	   R     A
	 *  I  I     S
	 *  D  M     E
	 *  X  E     S
	 *  
	 *  0  1      x
	 *  1  2      x
	 *  2  3      x
	 *  3  5      x 
	 *  4  7	  x
	 *  5 11 -> 1,3,7
	 *  6 13 -> 1,5,7
	 *  7 17 -> 1,5,11
	 *  8 19 -> 1,7,11
	 *  9 23 -> 5,7,11 *  [* note contiguous primes];   3,7,13
	 * 10 29 -> 1,11,17; 5,11,13
	 * 11 31 -> 7,11,13 *
	 * 12 37 -> 7,13,17
	 * 13 41 -> 11,13,17 *;  5,13,23
	 * 14 43 -> 7,17,19
	 * 15 47 -> 7,17,23; 5,19,23
	 * 16 53 -> 11,19,23
	 * 17 59 -> 17,19,23 *
	 * 18 61 -> 1,29,31
	 * 19 67 -> 7,29,31
	 * 20 71 -> 11,29,31
	 * 21 73 -> 13,29,31
	 * 22 79 -> 19,29,31
	 * 23 83 -> 23,29,31 *
	 * 24 89 -> 19,29,41
	 * 25 97 -> 13,41,43; 19,37,41
	 * 26 101-> 23,37,41
	 * 27 103->
	 * 28 107-> 
	 * 29 109-> 31,37,41 *	
	 */
	void reducePrime(PrimeRefIntfc prime)
	{
		System.out.println("reduce prime - processing " + prime.getPrime() + "  idx " + prime.getPrimeRefIdx());
				
		PrimeRefIntfc [] vals = new PrimeRefIntfc[3];
		
		initValues(prime, vals);
		
		int sign;
		do
		{
			System.out.println(" processing " + prime.getPrime() + " set:" + Arrays.asList(vals).stream().map(p -> p.getPrime().toString()).collect(Collectors.joining(",")));
			
			sign = getAllTotal(vals).subtract(prime.getPrime()).signum(); 
			if (sign == 1)
			{
				vals[mid] = vals[mid].getPrevPrimeRef();
			}
			else if (sign == -1)
			{
				vals[bot] = vals[bot].getNextPrimeRef();
			}

			System.out.println(Arrays
					.asList(vals)
					.stream()
					.map(p -> p.getPrime().toString()).collect(Collectors.joining(",", "[", "]")));	
		} while(sign != 0);
		
		addPrimeBases(prime, vals);
		System.out.println(String.format("Prime %d set %s", prime.getPrime(),   Arrays.asList(vals).stream().map(p -> p.getPrime().toString()).collect(Collectors.joining(",", "[", "]"))));	
	}

	void addPrimeBases(PrimeRefIntfc prime, PrimeRefIntfc [] vals)
	{
		BitSet bs = new BitSet();
		for (PrimeRefIntfc p : vals)
		{
			bs.set(p.getPrimeRefIdx());
		}
		prime.addPrimeBase(bs);
	}
	
	BigInteger getAllTotal(PrimeRefIntfc [] vals)
	{
		return Arrays.asList(vals).stream().map(PrimeRefIntfc::getPrime).reduce(BigInteger.ZERO, (a,b) -> a.add(b));
	}
	
	BigInteger getTop2Total(PrimeRefIntfc [] vals)
	{
		return vals[top].getPrime().add(vals[mid].getPrime());  
	}
	
	BigInteger getLWM(PrimeRefIntfc prime, PrimeRefIntfc [] vals)
	{
		return vals[bot].getPrime();
	}
	
	/**
	 * Init High Water Mark - maximal potential for: Top + Mid < Target
	 * @return
	 */
	BigInteger initHWM(PrimeRefIntfc prime, PrimeRefIntfc [] vals)
	{
		BigInteger hwm = getTop2Total(vals);
		while (hwm.subtract(prime.getPrime()).signum() == 1) 
		{
			hwm = vals[top].getPrime().add(vals[mid].getPrime());
			vals[top] = vals[top].getPrevPrimeRef();
			vals[mid] = vals[top].getPrevPrimeRef();
		};	
		return hwm;
	}
	
	BigInteger initValues(PrimeRefIntfc prime, PrimeRefIntfc [] vals)
	{
		BigInteger half = prime.getPrime().divide(BigInteger.TWO);
		vals[top] = ps.getPrimeRef(ps.getNextHighPrimeIdx(half)); 
		vals[mid] = ps.getPrimeRef(ps.getNextLowPrimeIdx(half)); 
		vals[bot] = ps.getPrimeRef(0);
		
		System.out.println("half of prime:" + half + " set:" + Arrays.asList(vals).stream().map(p -> p.getPrime().toString()).collect(Collectors.joining(",")));
		
		return initHWM(prime, vals);
	}
	
	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	public void log3Base(int activeBaseId)
	{
		System.out.println("log3base entry");
		// Bootstrap
		final int minPrimeIdx = ps.getPrimeIdx(BigInteger.valueOf(11L));
		// Process
		for(int curPrimeIdx=minPrimeIdx; curPrimeIdx < ps.getMaxIdx(); curPrimeIdx++) 
		{ 
			PrimeRefIntfc curPrime = ps.getPrimeRef(curPrimeIdx);	
			try
			{
				reducePrime(curPrime);
			}
			catch(Exception e)
			{
				log.severe(String.format("log3Base generation => prime-idx [%d] prime [%d] error: %s", curPrimeIdx, curPrime.getPrime(), e.toString()));
				break;
			}				
		}

		for (int curPrimeIdx = 0; curPrimeIdx < minPrimeIdx; curPrimeIdx++)
		{
			BitSet bNew = new BitSet();
			bNew.set(0);
			ps.getPrimeRef(curPrimeIdx).addPrimeBase(bNew);
		}
		
		// Get desired data
		ps.setActiveBaseId(activeBaseId);
		for (int i = 0; i < ps.getMaxIdx(); i++)
		{ 				
			PrimeRefIntfc pr = ps.getPrimeRef(i);
			try 
			{
				System.out.println(String.format("Prime [%d] idx[%d] bases %s base-indexes %s",
						pr.getPrime(), 
						i,
						pr.getIdxPrimes(),
						pr.getIndexes()));
			}
			catch(Exception e)
			{
				System.out.println(String.format("Can't show bases for: %d", pr.getPrime()));
			}		
		}	
	}	
}
