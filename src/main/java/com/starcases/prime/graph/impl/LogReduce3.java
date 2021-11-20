package com.starcases.prime.graph.impl;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;
import picocli.CommandLine.Command;

// 
// start 1
//       2
//       3 <-  2 + 1
//       5 <-  3 + 2
//       7 <-  5 + 2
//       11 <- 7+3+1; 5+3+2+1
@Log
public class LogReduce3 extends PrimeGrapher implements LogGraphIntfc
{
	static Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));

	public LogReduce3(PrimeSourceIntfc ps)
	{
		super(ps, log);
	}
	
	/*
	private String descOp(	String when,
							String initialPrimes,
							String initialIndexes,							
							BigInteger initialSum,
							Integer [] idx3Offsets,
							Integer [] tmpCalcedIdxs,
							BigInteger [] tmpPrimes,
							BigInteger tmpTotalSum,
							BigInteger tmpDiff,  
							BigInteger requiredDiff, 
							BigInteger targetPrime)
	{	
		return String.format("%5s init-idxs[%7s] init-prim[%s] init-sum[%3d] Idx-off-chgs[%8s] ## tmp-idxs[%8s] tmp-prim[%8s] tmp-ttl-sum[%d] tmp-ttl-diff[%d] *** target-diff[%d] target-prime[%d]",
				when, 																											// when
				initialIndexes,																										// init idxs
				initialPrimes,																										// init set of primes to sum
				initialSum,																											// init totalSum
				
				Arrays.asList(idx3Offsets).stream().map(i -> Integer.toString(i)).collect(Collectors.joining(",")), 				// idx3offsets
				Arrays.asList(tmpCalcedIdxs).stream().map(i -> Integer.toString(i)).collect(Collectors.joining(",")), 			// tmp3indexes				
				Arrays.asList(tmpPrimes).stream().map(BigInteger::toString).collect(Collectors.joining(",")), 				// tmpPrimes	
				tmpTotalSum,
				tmpDiff,																											// sum-offset
				requiredDiff, 																									// required diff
				targetPrime);																									// target prime
	}
	*/

	/**
	 * 
	 * @param targetDiff
	 * @param idx3Offsets
	 * @param sum3
	 * @param sum3Idxs
	 * @param targetPrime
	 * @param func
	 * @return 
	 */
	/*
	private Optional<BigInteger> handleIncrement(BigInteger targetDiff, 
												Integer [] idx3Offsets,  
												BigInteger [] sum3, 
												Integer [] sum3Idxs, 
												BigInteger targetPrime, 
												BiFunction<BigInteger, BigInteger, Boolean> func)
	{	
		Integer [] tmpCalcedIdxs = { sum3Idxs[0] + idx3Offsets[0], sum3Idxs[1] + idx3Offsets[1],sum3Idxs[2] + idx3Offsets[2]};
		
		if (Arrays.asList(tmpCalcedIdxs).stream().distinct().count() != 3)
		{
			System.out.println("idx overlap: " + Arrays.asList(tmpCalcedIdxs).stream().map(i -> Integer.toString(i)).collect(Collectors.joining(",", "[","]")));
			return Optional.empty();
		}
		
		BigInteger [] tmpPrimes = {
				ps.getPrime(tmpCalcedIdxs[0]),
				ps.getPrime(tmpCalcedIdxs[1]),
				ps.getPrime(tmpCalcedIdxs[2])
		};
		
		BigInteger [] tmpDiffsNextPrime = 
			{ 
				tmpPrimes[0].subtract(ps.getPrime(sum3Idxs[0])), 
				tmpPrimes[1].subtract(ps.getPrime(sum3Idxs[1])),
				tmpPrimes[2].subtract(ps.getPrime(sum3Idxs[2]))
			};
		
		BigInteger tmpTotalDiff = Arrays.asList(tmpDiffsNextPrime).stream().reduce(BigInteger.ZERO, BigInteger::add);
		BigInteger tmpTotalSum = Arrays.asList(tmpPrimes).stream().reduce(BigInteger.ZERO, BigInteger::add);
		
		BigInteger initialSum = Arrays.asList(sum3).stream().reduce(BigInteger.ZERO, BigInteger::add);
		
		String initSum3Idxs = Arrays.asList(sum3Idxs).stream().map(i -> Integer.toString(i)).collect(Collectors.joining(","));
		String initPrimes = Arrays.asList(sum3).stream().map(BigInteger::toString).collect(Collectors.joining(","));
		System.out.println(descOp("Start", initPrimes, initSum3Idxs, initialSum, idx3Offsets, tmpCalcedIdxs, tmpPrimes, tmpTotalSum, tmpTotalDiff, targetDiff, targetPrime));
		
		if (func.apply(tmpTotalDiff, targetDiff))
		{
			for (int i=0; i< sum3Idxs.length; i++)
			{
				sum3Idxs[i] = tmpCalcedIdxs[i];
			}
			BigInteger totalSum = getTotalSum(sum3, sum3Idxs);
			System.out.println(descOp("Post",initPrimes, initSum3Idxs, initialSum, idx3Offsets, tmpCalcedIdxs, tmpPrimes, tmpTotalSum, tmpTotalDiff, targetDiff, targetPrime));
			return Optional.of(totalSum);
		}
		return Optional.empty();
	}
	
	Integer [][] buildpermutations(int bits, int [] vals)
	{
		
		int rows = (int)Math.pow(bits, vals.length);
		Integer [][] result = new Integer[bits][];
		for (int i=0; i < rows; i++)
		{
			result[i] = new Integer[bits];
		}
		return result;
	}
	*/
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
	 * 21 73 -> 113,29,31
	 * 22 79 -> 19,29,31
	 * 23 83 -> 23,29,31 *
	 * 24 89 -> 19,29,41
	 * 25 97 -> 13,41,43; 19,37,41
	 * 26 101-> 23,37,41
	 * 27 103->
	 * 28 107-> 
	 * 29 109-> 31,37,41 *	
	 */
	/*
	Consumer<BitSet> log3BaseReducer(Integer primeIdx, BitSet bs) 
		{
			Integer targetPrimeIdx = primeIdx;
			BigInteger targetPrime = ps.getPrime(targetPrimeIdx);
				
			// Programmatic guess at starting point indexes versus brute force entire range
			BigInteger tmpVal = targetPrime.shiftRight(1).subtract( targetPrime.shiftRight(2));
			Integer spIdx = ps.getNextLowPrime(tmpVal);
			
			// values for each index which are used in various permutations to determine changes needed to reach targetPrime
			Integer [] idx3 = {spIdx, spIdx+1, spIdx+2};
			BigInteger [] sum3 = new BigInteger[3];
			BigInteger totalSum = getTotalSum(sum3, idx3); // initialize the primes at at idx and total it
		
			// allowed adjustments [unless result in idx overlap]
			Integer [][] posOffsets = {
					{1,1,1},  // incAllIdx
					{0,1,1},   // incTop2Idx
					{-1, 1, 1},
					{1, 0, 1},
					{0,0,1},
					{-1, 0, 1},
					{1, -1, 1},
					{0, -1, 1},
					{1,1,0},
					{0,1,0}
					
					
			};
			
			Integer [][] negOffsets = {
					{-1,-1,-1},	
					 {-1,-1, 0},
					 {-1, 0, 0},
					 {-1,-1, 1}
			};
			
			do
			{
				Optional<BigInteger> newSum = Optional.empty();
				final BigInteger requiredDiff = totalSum.subtract(targetPrime); 
				if (requiredDiff.signum() == 0)
				{
					break;
				}
				else if (requiredDiff.signum() == -1)
				{
					newSum =  Arrays.asList(posOffsets)
							.stream()
							.map(offset ->	handleIncrement(requiredDiff, offset ,  sum3, idx3, targetPrime, (a, b) -> {return a.add(b).equals(BigInteger.ZERO);}))
							.filter(Optional::isPresent)
							.map(Optional::get)
							.findAny();
				}
				else 
				{
					newSum = Arrays.asList(negOffsets)
							.stream()
							.map(offset ->	handleIncrement(requiredDiff, offset ,  sum3, idx3, targetPrime, (a, b) -> {return a.add(b).equals(BigInteger.ZERO);}))
							.filter(Optional::isPresent)
							.map(Optional::get)
							.findAny();				
				}
				
				if (newSum.isPresent())
				{
					totalSum = newSum.get();				
				}	
			} 
			while(true);	
		
			System.out.println(String.format("Added base to Prime[%d] Idx[%d]", targetPrime, targetPrimeIdx));
			
			return (bsTmp) -> {
				BitSet bNew = new BitSet();
				Arrays.asList(idx3).stream().forEach(bNew::set);
			};
		};
		*/

	/**
	 *
	 * @param idx idx current index to reduce;
	 * @param reducer function implementing reduction algo
	 */
	/*
	private void log3BaseReduction(BitSet bs, Consumer<BitSet> log3BaseReducer)
	{	
		log3BaseReducer.accept(bs);
	}	
	*/
	void reducePrime(PrimeRefIntfc prime)
	{
		System.out.println("reduce prime - processing " + prime.getPrime() + "  idx " + prime.getPrimeRefIdx());
		
		int top = 0;
		int mid = 1;
		int bot = 2;
		
		PrimeRefIntfc [] vals = new PrimeRefIntfc[3];
		vals[top] = ps.getPrimeRef(prime.getPrimeRefIdx()-1);
		vals[mid] = ps.getPrimeRef(prime.getPrimeRefIdx()-2);
		vals[bot] = ps.getPrimeRef(0);
		
		BigInteger max;
		int sign = 0;
		do 
		{
			System.out.println("loop top:" + vals[top].getPrime() + "  idx:" + vals[top].getPrimeRefIdx());
			max = vals[top].getPrime().add(ps.getPrime(vals[mid].getPrimeRefIdx()));
			System.out.println("loop max " + max);
			sign = max.subtract(prime.getPrime()).signum();
			System.out.println("sign " + sign);
			if (sign == 1)
			{
				System.out.println("vals top " + vals[top].getPrime() + "  idx:" + vals[top].getPrimeRefIdx());
				vals[top] = ps.getPrimeRef(vals[top].getPrimeRefIdx()-1);
				vals[mid] = ps.getPrimeRef(vals[top].getPrimeRefIdx()-1);
				max = vals[top].getPrime().add(ps.getPrime(vals[mid].getPrimeRefIdx()));
			}
			else break;

		} while(sign == 1);
		
		if (sign == -1)
		{
			do
			{
				BigInteger min = vals[bot].getPrime().add(ps.getPrimeRef(vals[bot].getPrimeRefIdx()+1).getPrime());
				System.out.println("min " + min + "  vals[top]" + vals[top].getPrime());
				sign = min.add(vals[top].getPrime()).subtract(prime.getPrime()).signum();
				if (sign == 1)
				{
					vals[top] = ps.getPrimeRef(vals[top].getPrimeRefIdx()-1);
					vals[mid] = ps.getPrimeRef(vals[mid].getPrimeRefIdx()-1);
				}
				else if (sign == -1)
				{
					vals[bot] = ps.getPrimeRef(vals[bot].getPrimeRefIdx()+1);
				}
				else
					break;
			} while(true);
		}
		
		System.out.println(String.format("Prime %d set %s", prime.getPrime(),   Arrays.asList(vals).stream().map(p -> p.getPrime().toString()).collect(Collectors.joining(",", "[", "]"))));	
	}
	
	@Override
	public void log()
	{
		//TODO
	}
	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	public void log3Base(int activeBaseId)
	{
		System.out.println("log3base entry");
		// Bootstrap
		final int minPrimeIdx = ps.getPrimeIdx(BigInteger.valueOf(71L));
		System.out.println("log3base min prime idx:" + minPrimeIdx);
		// Process
		for(int curPrimeIdx=minPrimeIdx; curPrimeIdx < ps.getMaxIdx(); curPrimeIdx++) 
		{ 
			System.out.println("get prime ref - pre");
			PrimeRefIntfc curPrime = ps.getPrimeRef(curPrimeIdx);	
			System.out.println("get prime ref - post");
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
