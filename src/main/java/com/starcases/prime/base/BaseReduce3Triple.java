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
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.BiPredicate;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;

enum TripleIdx { TOP, MID, BOT}

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
 * 27 103->
 * 28 107-> 
 * 29 109-> 31,37,41 *	
 */
@Log
public class BaseReduce3Triple extends AbstractPrimeBase
{
	static final Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));

	BaseTypes activeBaseId;
	
	public BaseReduce3Triple(PrimeSourceIntfc ps)
	{
		super(ps, log);
		
		if (!ps.baseExist(BaseTypes.DEFAULT))
			(new BaseReduceNPrime(ps)).genBases();
		
		activeBaseId = BaseTypes.THREETRIPLE;
		ps.setActiveBaseId(activeBaseId);
	}
	
	/**
	 * Provide a form of back-tracking by saving state and restoring it on determination of non-viable later state.
	 * @param fn
	 * @param vals
	 * @return
	 */
	private State ensureConstraints(			
			Consumer<PrimeRefIntfc[]> fn, 
			BiFunction<PrimeRefIntfc[], BigInteger, State> pred,
			PrimeRefIntfc [] vals,
			BigInteger diff)
	{
		var saved = Arrays.copyOf(vals, vals.length);	
		fn.accept(vals);
			
		var ret = pred.apply(vals, diff);
		
		if (State.EQUAL != ret)  // revert change if needed
		{
			System.arraycopy(saved, 0, vals, 0, saved.length);
		}
		
		return ret;
	}
	
	
	private BiFunction<PrimeRefIntfc[], BigInteger, State> getFunc(
																Consumer<PrimeRefIntfc[]> consumer,
																BiFunction<PrimeRefIntfc[], BigInteger, State> pred
																)
	{
		return  (valArray, d) -> ensureConstraints(consumer, pred, valArray, d);
	}
	
	private void reducePrime(PrimeRefIntfc prime)
	{	
		var vals = new PrimeRefIntfc[3];
		
		BigInteger sum = initValues(prime, vals);
		sum = initHWM(prime, vals, sum);
		sum = initLWM(prime, vals, sum);

		String s = Arrays.asList(vals).stream().map(b -> b.toString()).collect(Collectors.joining(",", "[","]"));
		log.info(String.format("Prime %d  sum %d vals:%s", prime.getPrime(), sum, s));
		var neglist = new ArrayList<Function<PrimeRefIntfc[], State >>();
		
		//neglist.add( getFunc( 
	//							v -> { add(TripleIdx.BOT.ordinal(), v);},
	//							(v, d) -> { boolean r= getAllSum(v)  }
	//						) );
		
		var stopNow = prime.getPrime().compareTo(sum) == 0;
		while (!stopNow)  // Not the prettiest code; look for a nicer refactoring.
		{
			final var diff = prime.getPrime().subtract(getAllSum(vals));
			final var sign = diff.signum();
			
		/*	if (sign == -1) // prime < total , diff should be negative
			{	
				// what is dist to next +/- prime (not overlapping other array values)
				//      H
				//      M
				//      L
				
				// if sum low
				//    Can H++ match prime
				//    can m++ match prime
				//    can L++ match prime
				//
				
				if (!(stopNow = ensureConstraints(prime, f -> add(BOT, diff, vals), vals)))				
					;
				if (!(stopNow = ensureConstraints(prime, f -> add(MID, diff, vals), vals)))
					;
				if (!(stopNow = ensureConstraints(prime, f -> add(TOP, diff, vals), vals)))
					;
				if (!(stopNow = ensureConstraints(prime, f -> { vals.set(MID, vals.get(MID).getPrevPrimeRef().get());   add(BOT, diff, vals); }, vals)))
					;
				if (!(stopNow = ensureConstraints(prime, f -> { vals.set(TOP, vals.get(TOP).getPrevPrimeRef().get());   add(MID, diff, vals); }, vals)))
					;
				if (!(stopNow = ensureConstraints(prime, f -> { vals.set(TOP, vals.get(TOP).getPrevPrimeRef().get());   add(BOT, diff, vals); }, vals)))
						;
			} 
			else if (sign == 1) // prime > total, diff should be positive
			{				
				if (!(stopNow = ensureConstraints(prime, f -> add(TOP, diff, vals), vals)))
					;
				if (!(stopNow = ensureConstraints(prime, f -> add(MID, diff, vals), vals)))
					;
				if (!(stopNow = ensureConstraints(prime, f -> add(BOT, diff, vals), vals)))
					;
				if (!(stopNow = ensureConstraints(prime, f -> {   vals.set(TOP, vals.get(TOP).getPrevPrimeRef().get());
																		vals.set(BOT, vals.get(BOT).getNextPrimeRef().get());
																		add(MID, diff, vals); }, vals)))
				{
					stopNow = ensureConstraints(prime  f -> {   vals.set(MID, vals.get(MID).getPrevPrimeRef().get().getPrevPrimeRef().get());
					vals.set(BOT, vals.get(BOT).getNextPrimeRef().get());
					 });
				}

			}
			else
			{			
				break;
			} */
			
			if (prime.getPrime().compareTo(getAllSum(vals)) != 0)
				stopNow = false;

			if (doLog && log.isLoggable(Level.INFO))
			{
				log.info(String.format("sign %d reduce prime - processing [%d] idx [%d] diff[%d] ", sign, prime.getPrime(), prime.getPrimeRefIdx(), diff));
				break;
			}
		}
		
		if (stopNow)
			addPrimeBases(prime, vals);
		
		if (doLog && log.isLoggable(Level.INFO))
		{
			//log.info(String.format("Prime %d set %s  is-equal=%b", prime.getPrime(), getValSet(vals), getAllSum(vals).compareTo(prime.getPrime()) == 0));
		}
	}

	private BigInteger initValues(PrimeRefIntfc prime, PrimeRefIntfc [] vals)
	{
		var topMuliplier = new BigDecimal("0.4");
		var upperFraction = (new BigDecimal(prime.getPrime())).multiply(topMuliplier);
		vals[TripleIdx.TOP.ordinal()] = ps.getNearPrimeRef(upperFraction).get(); 			// search higher
		vals[TripleIdx.MID.ordinal()] = ps.getNearPrimeRef(upperFraction.negate()).get(); 	// search lower
		var sum = getAllSum(vals[TripleIdx.TOP.ordinal()], vals[TripleIdx.MID.ordinal()]);
		log.info(String.format("initValues: prime: %d  sum: %d  vals: %s", prime.getPrime(), sum, Arrays.asList(vals[TripleIdx.TOP.ordinal()] , vals[TripleIdx.MID.ordinal()]).stream().map(i -> i.toString()).collect(Collectors.joining(",", "[", "]"))));
		return sum;
	}
	
	/**
	 * Init High Water Mark - maximal potential for: Top + Mid < Target
	 * vals must be length 3
	 * @return
	 */
	private BigInteger initHWM(PrimeRefIntfc prime, PrimeRefIntfc [] vals, BigInteger sum)
	{
		BigInteger hwm =  getAllSum(
				vals[TripleIdx.TOP.ordinal()],
				vals[TripleIdx.MID.ordinal()]);
		
		int sign = hwm.subtract(prime.getPrime()).signum();
		
		if (sign < 0) // normalize to above prime
		{
			do
			{
				vals[TripleIdx.TOP.ordinal()].getNextPrimeRef().ifPresent( ppr -> vals[TripleIdx.TOP.ordinal()]  = ppr);
				vals[TripleIdx.MID.ordinal()].getNextPrimeRef().ifPresent( ppr -> vals[TripleIdx.MID.ordinal()] = ppr);
			}
			while ((hwm = getAllSum(
					vals[TripleIdx.TOP.ordinal()],
					vals[TripleIdx.MID.ordinal()]) 
			).subtract(prime.getPrime()).signum() < 0); 
			
		}
		
		// reduce to below prime
		while ((hwm = getAllSum(
				vals[TripleIdx.TOP.ordinal()],
				vals[TripleIdx.MID.ordinal()]) 
				).subtract(prime.getPrime()).signum() == 1)
		{
			var overage = sum.subtract(prime.getPrime());
			
			vals[TripleIdx.TOP.ordinal()].getPrevPrimeRef().ifPresent( ppr -> vals[TripleIdx.TOP.ordinal()]  = ppr);
			
			ps.getNearPrimeRef( vals[TripleIdx.MID.ordinal()].getPrime().subtract(overage)).ifPresent( ppr -> vals[TripleIdx.MID.ordinal()] = ppr);
			log.info(String.format("initHWM: prime: %d  sum: %d  vals: %s", prime.getPrime(), getAllSum(vals), Arrays.asList(vals).stream().filter(i -> i != null).map(i -> i.toString()).collect(Collectors.joining(",", "[", "]"))));
			break;
		}
					
		return hwm;
	}
	
	/**
	 * calc first pass at BOT/MID given fixed high value. 
	 * 
	 * Must provide the array with length 3
	 * 
	 * @param vals
	 */
	private BigInteger initLWM(PrimeRefIntfc prime, PrimeRefIntfc [] vals, BigInteger sum)
	{
		Optional<PrimeRefIntfc> remainder = ps.getNearPrimeRef(sum.subtract(prime.getPrime()));
		if (remainder.isPresent())
		{
			vals[TripleIdx.BOT.ordinal()] = remainder.get();
			
		}
			
		//var botMultiplier = new BigDecimal("0.23");
		//var botCalc = (new BigDecimal(vals[TripleIdx.TOP.ordinal()].getPrime())).multiply(botMultiplier);
			
		//vals[TripleIdx.MID.ordinal()] = ps.getNearPrimeRef(botCalc).get(); 			// search higher
	//	vals[TripleIdx.BOT.ordinal()] = ps.getNearPrimeRef(botCalc.negate()).get(); // search lower
		return getAllSum(vals);
	}
	

	
	private PrimeRefIntfc add(TripleIdx idx, BigInteger diff, PrimeRefIntfc [] vals)
	{
		var tmp = ps.getPrimeRef(vals[idx.ordinal()].getPrime().add(diff));
		tmp.ifPresent(p -> vals[idx.ordinal()] = p);
		return vals[idx.ordinal()];
	}
	
	private void addPrimeBases(PrimeRefIntfc prime, PrimeRefIntfc...vals)
	{
		var bs = new BitSet();
		for (var p : vals)
		{
			bs.set(p.getPrimeRefIdx());
		}
		prime.addPrimeBase(bs, BaseTypes.THREETRIPLE);
	}
	
	/**
	 * Get sum of all specified prime ref vals
	 * 
	 * @param vals
	 * @return
	 */
	private BigInteger getAllSum(PrimeRefIntfc...vals)
	{
		return Arrays.asList(vals).stream().filter(p -> p != null).map(PrimeRefIntfc::getPrime).reduce(BigInteger.ZERO, BigInteger::add);
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
			counter++;
			var bNew = new BitSet();
			bNew.set(0);
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
	}	
}
