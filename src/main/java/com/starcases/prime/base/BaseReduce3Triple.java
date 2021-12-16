package com.starcases.prime.base;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Optional;
import java.util.Objects;
import java.util.Stack;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.starcases.prime.impl.AbstractPrimeRef;
import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;

enum TripleIdx { TOP, MID, BOT}

class  Bases3 extends AbstractPrimeRef
{
	public Bases3(int primeIdx)
	{
		super(primeIdx);
	}
}
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
	
	Stack<PrimeRefIntfc[]> backTrackStack = new Stack<>();
	
	
	BiFunction<PrimeRefIntfc[], BigInteger, State> funcNextTop = 
			getFunc(
					valsp -> valsp[TripleIdx.TOP.ordinal()].getNextPrimeRef().ifPresent( ppr -> valsp[TripleIdx.TOP.ordinal()]  = ppr),
					(valsp, primep) -> { 
								var s = getAllSum(valsp); 
								return switch (s.compareTo(primep) )
										{
											case 0 ->  State.EQUAL;
											case 1 -> State.OVER;
											case -1 -> State.UNDER;
											default -> State.REVERT;
										};
							}
			);
	
	BiFunction<PrimeRefIntfc[], BigInteger, State> funcNextMid = 
			getFunc(
					valsp -> valsp[TripleIdx.MID.ordinal()].getNextPrimeRef().ifPresent( ppr -> valsp[TripleIdx.MID.ordinal()]  = ppr),
					(valsp, primep) -> { 
								var s = getAllSum(valsp); 
								return switch ( (ps.distinct(valsp) ? 0x0 : 0xff) |s.compareTo(primep) )
										{
											case 0 ->  State.EQUAL;
											case 1 -> State.OVER;
											case -1 -> State.UNDER;
											default -> State.REVERT;
										};
							}
			);

	BiFunction<PrimeRefIntfc[], BigInteger, State> funcPrevTop = 
			getFunc(
					valsp -> valsp[TripleIdx.TOP.ordinal()].getPrevPrimeRef().ifPresent( ppr -> valsp[TripleIdx.TOP.ordinal()]  = ppr),
					(valsp, primep) -> { 
								var s = getAllSum(valsp); 
								return switch ( (ps.distinct(valsp) ? 0x0 : 0xff) | s.compareTo(primep) )
										{
											case 0 ->  State.EQUAL;
											case 1 -> State.OVER;
											case -1 -> State.UNDER;
											default -> State.REVERT;
										};
							}
			);
	
	BiFunction<PrimeRefIntfc[], BigInteger, State> funcPrevMid = 
			getFunc(
					valsp -> valsp[TripleIdx.MID.ordinal()].getPrevPrimeRef().ifPresent( ppr -> valsp[TripleIdx.MID.ordinal()]  = ppr),
					(valsp, primep) -> { 
								var s = getAllSum(valsp); 
								return switch ( (ps.distinct(valsp) ? 0x0 : 0xff) | s.compareTo(primep) )
										{
											case 0 ->  State.EQUAL;
											case 1 -> State.OVER;
											case -1 -> State.UNDER;
											default -> State.REVERT;
										};
							}
			);	
	
	public BaseReduce3Triple(PrimeSourceIntfc ps)
	{
		super(ps, log);
		
		if (!ps.baseExist(BaseTypes.DEFAULT))
			(new BaseReduceNPrime(ps)).genBases();
		
		activeBaseId = BaseTypes.THREETRIPLE;
		ps.setActiveBaseId(activeBaseId);
	}
	
	private void saveVals(PrimeRefIntfc [] vals)
	{
		var saved = Arrays.copyOf(vals, vals.length);
		backTrackStack.push(saved);
	}
	
	private void revertVals(PrimeRefIntfc [] vals)
	{
		var saved = backTrackStack.pop();
		System.arraycopy(saved, 0, vals, 0, saved.length);
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
		saveVals(vals);
		fn.accept(vals);
			
		var ret = pred.apply(vals, diff);
		
		if (State.REVERT == ret)  // revert change if needed
		{
			revertVals(vals);
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
		
		var stopNow = prime.getPrime().compareTo(sum) == 0;
		while (!stopNow)
		{
			final var diff = prime.getPrime().subtract(getAllSum(vals));
			final var sign = diff.signum();
			
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

	/**
	 * Initially:
	 * 		top & mid bases should are adjacent
	 * 		bot base starts at prime 1
	 * 
	 *  if sum > prime then reduce mid
	 *  if sum < prime then raise bot to make up the difference.
	 * 
	 * @param prime
	 * @param vals
	 * @return
	 */
	private BigInteger initValues(PrimeRefIntfc prime, PrimeRefIntfc [] vals)
	{
		var topMuliplier = new BigDecimal("0.4");
		var upperFraction = (new BigDecimal(prime.getPrime())).multiply(topMuliplier);
		
		ps.getNearPrimeRef(upperFraction)		  .ifPresent(pr -> vals[TripleIdx.TOP.ordinal()] = pr); 	// search higher
		ps.getNearPrimeRef(upperFraction.negate()).ifPresent(pr -> vals[TripleIdx.MID.ordinal()] = pr); 	// search lower
		ps.getPrimeRef(BigInteger.ONE).ifPresent(pr -> vals[TripleIdx.BOT.ordinal()] = pr);					// set BOT to 1
		
		var sum = getAllSum(vals);
		
		if (sum.compareTo(prime.getPrime()) == 0)
			return sum;
		
		var diff = prime.getPrime().subtract(sum);
		
		if (diff.signum() > 0 ) // Prime larger than sum
		{
			vals[TripleIdx.BOT.ordinal()].getDistToNextPrime().ifPresent(p ->   p.compareTo(diff) == 0 ? this.f);
			this.
		
			ps.getNearPrimeRef(vals[TripleIdx.BOT.ordinal()].getPrime().add(diff)).ifPresent(pr -> vals[TripleIdx.BOT.ordinal()] = pr);
		}

		sum = getAllSum(vals);
		
		if (sum.compareTo(prime.getPrime()) == 0)
			return sum;
		
		diff = prime.getPrime().subtract(sum);
		
		
		if (diff.signum() < 0) // prime less than sum
		{
			// find a mid prime lower than current mid
			ps.getNearPrimeRef(vals[TripleIdx.MID.ordinal()].getPrime().add(diff)).ifPresent(pr -> vals[TripleIdx.MID.ordinal()] = pr);			
		}
		
		sum = getAllSum(vals);
		diff = prime.getPrime().subtract(sum);
		
		if (log.isLoggable(Level.INFO))
		{
			log.info(String.format("initValues: prime:[%d]  sum:[%d]  vals:[%s]  sum-prime-diff:[%d]", 
				prime.getPrime(), 
				sum, 
				Arrays.asList(vals).stream().filter(Objects::nonNull).map(Object::toString)
				.collect(Collectors.joining(",", "[", "]")),
				diff));
		}
		return sum;
	}
	
	/**
	 * Init High Water Mark - maximal potential for: Top + Mid < Target
	 * vals must be length 3
	 * @return
	 */
	private BigInteger initHWM(PrimeRefIntfc prime, PrimeRefIntfc [] vals, BigInteger sum)
	{
		var diff = prime.getPrime().subtract(sum);
		BigInteger hwm = sum;
		BigInteger p = prime.getPrime();
		while (diff.signum() > 0) // sum is lower than prime
		{
			funcNextTop.apply(vals, p);
			funcNextMid.apply(vals, p);
			
			hwm = getAllSum(vals);
			diff = prime.getPrime().subtract(hwm);
		}
		
		while (diff.signum() == 1) // reduce to below prime
		{
			funcPrevMid.apply(vals, p);
			funcPrevTop.apply(vals, p);
			
					
			hwm = getAllSum(vals);
			diff = prime.getPrime().subtract(hwm);
			
			if (log.isLoggable(Level.INFO))
				log.info(String.format("initHWM: prime: %d  sum: %d  vals: %s", prime.getPrime(), hwm, Arrays.asList(vals).stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(",", "[", "]"))));
		}
		
		if (log.isLoggable(Level.INFO))
			log.info(String.format("initHWM exit: prime: %d  sum: %d  vals: %s", prime.getPrime(), hwm, Arrays.asList(vals).stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(",", "[", "]"))));
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
		var hwm = sum;
		BigInteger diff = BigInteger.ZERO;
		diff = prime.getPrime().subtract(sum);
		
		if (log.isLoggable(Level.INFO))
			log.info(String.format("initLWM - enter: prime: %d  sum: %d  diff: %d vals: %s", prime.getPrime(), hwm, diff, Arrays.asList(vals).stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(",", "[", "]"))));
		if (prime.getPrime().compareTo(sum) != 0)
		{	
			Optional<PrimeRefIntfc> remainder = ps.getNearPrimeRef(diff);
			if (remainder.isPresent())
			{
				vals[TripleIdx.BOT.ordinal()] = remainder.get();
			}				
			hwm = getAllSum(vals);
			diff = prime.getPrime().subtract(hwm);
		}
		
		if (log.isLoggable(Level.INFO))
			log.info(String.format("initLWM - exiting: prime: %d  sum: %d  diff: %d vals: %s", prime.getPrime(), hwm, diff, Arrays.asList(vals).stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(",", "[", "]"))));
		return hwm;
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
		return Arrays.asList(vals).stream().filter(Objects::nonNull).map(PrimeRefIntfc::getPrime).reduce(BigInteger.ZERO, BigInteger::add);
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
