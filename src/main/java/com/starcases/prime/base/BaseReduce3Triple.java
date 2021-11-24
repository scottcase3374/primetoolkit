package com.starcases.prime.base;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.starcases.prime.graph.impl.PrimeGrapher;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;

@Log
public class BaseReduce3Triple extends PrimeGrapher 
{
	static final Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));
	static final MathContext mcFloor = new MathContext(1, RoundingMode.FLOOR);
	static final MathContext mcCeil = new MathContext(1, RoundingMode.CEILING);
	
	static final int TOP = 0;
	static final int MID = 1;
	static final int BOT = 2;
	
	public BaseReduce3Triple(PrimeSourceIntfc ps)
	{
		super(ps, log);
	}
	
	/*
	 *  given prime-idx, bitset
	 *  
	 *  find new bitset where bitset.size()-1 is reduced to the sum of
	 *  3 pre-existing primes. For example (not required responses but valid).
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
	private void reducePrime(PrimeRefIntfc prime)
	{	
		List<PrimeRefIntfc> vals = new ArrayList<>();
		
		initValues(prime, vals);
		initHWM(prime, vals);
		initLWM(vals);

		boolean stopNow = false;
		while (!stopNow)  // Not the prettiest code; look for a nicer refactoring.
		{
			final BigInteger diff = prime.getPrime().subtract(getAllSum(vals));
			final int sign = diff.signum();
			
			if (sign == -1) // prime < total , diff should be negative
			{				
				if (!(stopNow = ensureConstraints(prime, vals, f -> add(BOT, diff, vals))))				
					if (!(stopNow = ensureConstraints(prime, vals, f -> add(MID, diff, vals))))
						if (!(stopNow = ensureConstraints(prime, vals, f -> add(TOP, diff, vals))))
							if (!(stopNow = ensureConstraints(prime, vals, f -> { vals.set(MID, vals.get(MID).getPrevPrimeRef());   add(BOT, diff, vals); })))
								if (!(stopNow = ensureConstraints(prime, vals, f -> { vals.set(TOP, vals.get(TOP).getPrevPrimeRef());   add(MID, diff, vals); })))								
									stopNow = ensureConstraints(prime, vals, f -> { vals.set(TOP, vals.get(TOP).getPrevPrimeRef());   add(BOT, diff, vals); });
			} 
			else if (sign == 1) // prime > total, diff should be positive
			{				
				if (!(stopNow = ensureLowConstraints(prime, vals, f -> add(TOP, diff, vals))))				
					if (!(stopNow = ensureLowConstraints(prime, vals, f -> add(MID, diff, vals))))
						if (!(stopNow = ensureLowConstraints(prime, vals, f -> add(BOT, diff, vals))))
							stopNow = ensureConstraints(prime, vals, f -> {   vals.set(TOP, vals.get(TOP).getPrevPrimeRef());
																					vals.set(BOT, vals.get(BOT).getNextPrimeRef());
																					add(MID, diff, vals); });
			}
			else
			{			
				break;
			}
			
			if (prime.getPrime().compareTo(getAllTotal(vals)) != 0)
				stopNow = false;
			System.out.println(String.format("sign %d reduce prime - processing [%d] idx [%d] diff[%d] set: %s", sign, prime.getPrime(), prime.getPrimeRefIdx(), diff, getValSet(vals)));
		}
		
		addPrimeBases(prime, vals);
		System.out.println(String.format("Prime %d set %s  is-equal=%b", prime.getPrime(), getValSet(vals), getAllSum(vals).compareTo(prime.getPrime()) == 0));	
	}
	
	private PrimeRefIntfc add(int idx, BigInteger diff, List<PrimeRefIntfc> vals)
	{
		Optional<PrimeRefIntfc> tmp = ps.getPrime(vals.get(idx).getPrime().add(diff));
		tmp.ifPresent(p -> vals.set(idx, p));
		return vals.get(idx);
	}
	
	private String getValSet(List<PrimeRefIntfc> vals)
	{
		return vals.stream().map(p -> p.getPrime().toString()).collect(Collectors.joining(",", "[","]"));
	}
	
	private void addPrimeBases(PrimeRefIntfc prime, List<PrimeRefIntfc> vals)
	{
		BitSet bs = new BitSet();
		for (PrimeRefIntfc p : vals)
		{
			bs.set(p.getPrimeRefIdx());
		}
		prime.addPrimeBase(bs);
	}
	
	private BigInteger getAllTotal(List<PrimeRefIntfc> vals)
	{
		return vals.stream().map(PrimeRefIntfc::getPrime).reduce(BigInteger.ZERO, (a,b) -> a.add(b));
	}
	
	/**
	 * Init High Water Mark - maximal potential for: Top + Mid < Target
	 * @return
	 */
	private BigInteger initHWM(PrimeRefIntfc prime, List<PrimeRefIntfc> vals)
	{
		BigInteger hwm;
		while ((hwm = getAllSum(vals)).subtract(prime.getPrime()).signum() == 1) 
		{
			vals.set(TOP, vals.get(TOP).getPrevPrimeRef());
			vals.set(MID, vals.get(MID).getPrevPrimeRef());
		}	
		return hwm;
	}
	
	private void initLWM(List<PrimeRefIntfc>  vals)
	{
		BigDecimal num3 = BigDecimal.valueOf(3L);
		BigDecimal topTwoThird = (new BigDecimal(vals.get(TOP).getPrime())).divide(num3, RoundingMode.HALF_DOWN);
		vals.set(BOT, ps.getPrimeRef(ps.getNextLowPrimeIdx(topTwoThird)));
		
		vals.set(MID,  ps.getPrimeRef(ps.getNextLowPrimeIdx(vals.get(TOP).getPrime().subtract(vals.get(BOT).getPrime()))));
	}
	
	private void initValues(PrimeRefIntfc prime, List<PrimeRefIntfc> vals)
	{
		BigDecimal two = BigDecimal.valueOf(2L);
		BigDecimal half = (new BigDecimal(prime.getPrime())).divide(two);
		vals.add(ps.getPrimeRef(ps.getNextHighPrimeIdx(half))); 
		vals.add(ps.getPrimeRef(ps.getNextLowPrimeIdx(half))); 
		vals.add(ps.getPrimeRef(0));	
	}
	
	private BigInteger getAllSum(List<PrimeRefIntfc> vals)
	{
		return vals.stream().map(PrimeRefIntfc::getPrime).reduce(BigInteger.ZERO, BigInteger::add);
	}
	
	private boolean ensureConstraints(PrimeRefIntfc prime, List<PrimeRefIntfc> vals, Consumer<List<PrimeRefIntfc>> consumer)
	{
		PrimeRefIntfc [] saved = new PrimeRefIntfc[vals.size()];
		
		vals.toArray(saved);
		consumer.accept(vals);
		
		boolean ret = true;
		if (vals.stream().distinct().count() != 3)
		{
			ret = false;  // not distinct
		}
		
		if (ret && getAllSum(vals).compareTo(prime.getPrime()) == 1)
		{
			ret = false;  // sum exceeds rpime
		}
		
		if (!ret)  // revert change if needed
		{
			vals.clear();
			vals.addAll(Arrays.asList(saved));
		}
		
		return ret;
	}
	
	private boolean ensureLowConstraints(PrimeRefIntfc prime, List<PrimeRefIntfc> vals, Consumer<List<PrimeRefIntfc>> consumer)
	{
		PrimeRefIntfc [] saved = new PrimeRefIntfc[vals.size()];
		
		vals.toArray(saved);
		consumer.accept(vals);
		
		boolean ret = true;
		if (vals.stream().distinct().count() != 3)
		{
			ret = false; // not distinct
		}
		
		if (ret && getAllSum(vals).compareTo(prime.getPrime()) == -1)
		{
			ret = false; // sum below prime
		}
		
		if (!ret)  // revert change if needed
		{
			vals.clear();
			vals.addAll(Arrays.asList(saved));
		}
		
		return ret;
	}
	
	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	public void genBases(int activeBaseId)
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
	}	
}
