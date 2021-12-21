package com.starcases.prime.base;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;

/*
 *  Given a prime, find a bitset representing 3 pre-existing primes that sum to the prime. 
 *  
 *  Algorithm TRIES to avoid use of the initial values as bases- 1, 2, 3.
 *  
 *  For example (not required responses but valid).
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
 * 27 103-> 19,41,43
 * 28 107-> 
 * 29 109-> 31,37,41 *	
 */
@Log
public class BaseReduceTriple extends AbstractPrimeBase
{
	static final Comparator<String> nodeComparator = (o1,o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));

	BaseTypes activeBaseId;
	
	public BaseReduceTriple(PrimeSourceIntfc ps)
	{
		super(ps, log);
		
		activeBaseId = BaseTypes.THREETRIPLE;
		ps.setActiveBaseId(activeBaseId);
	}
	
	private void reducePrime(PrimeRefIntfc prime)
	{	
		Triple triple = 
				new Triple(
						ps, 
						prime, 
						p1 -> 
							{ 
								var multip = "0.4";  
								return ps.getNearPrimeRef((new BigDecimal(multip)).multiply(new BigDecimal(prime.getPrime()))).get(); 
							},
						p2 ->
							{
								var multip = "0.2";
								return ps.getNearPrimeRef((new BigDecimal(multip)).multiply(new BigDecimal(prime.getPrime())).negate()).get(); 	
							});
		
		triple.process().ifPresent( primes ->
										{
											log.warning(String.format("prime %d == sum %d: %s", 
													prime.getPrime(), 
													triple.sum(), 
													prime.getPrime().compareTo(triple.sum()) == 0));
											addPrimeBases(prime, primes);	
										}		
				);
		
	}

	private void addPrimeBases(PrimeRefIntfc prime, PrimeRefIntfc...vals)
	{
		var bs = new BitSet();
		Arrays.asList(vals).stream().filter(Objects::nonNull).map(PrimeRefIntfc::getPrimeRefIdx).forEach(bs::set);
		prime.addPrimeBase(bs, BaseTypes.THREETRIPLE);
	}
	
	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	public void genBases()
	{
		int counter = 0;
		if (doLog)
			log.entering("BaseReduce3Triple", "genBases()");
		
		BigInteger seven = BigInteger.valueOf(7L);
		final var minPrimeIdx = ps.getPrimeIdx(seven);
		
		Iterator<PrimeRefIntfc> pRefIt = ps.getPrimeRefIter();
		
		// handle Bootstrap values - can't really represent < 11 with a sum of 3 primes
		while(pRefIt.hasNext())
		{
			var curPrime = pRefIt.next();			
			var bNew = new BitSet();
			bNew.set(counter);
			counter++;
			curPrime.addPrimeBase(bNew, BaseTypes.THREETRIPLE);
			if (curPrime.getPrimeRefIdx() == minPrimeIdx)
				break;
		}
		
		// Process
		while (pRefIt.hasNext()) 
		{ 
			var curPrime = pRefIt.next();
			counter++;
			try
			{
				reducePrime(curPrime);
			}
			catch(Exception e)
			{
				log.severe(String.format("BaseReduce3Triple generation => idx[%d] prime [%d] error: %s", counter, curPrime.getPrime(), e.toString()));
				e.printStackTrace();
				break;
			}				
		}
		
		if (log.isLoggable(Level.INFO))
			log.info(String.format("Total valid entries: %d out of %d",  + Triple.good, ps.getMaxIdx()));
	}	
}
