package com.starcases.prime.base.nprime;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Deque;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;

/**
 * Note, this is output as part of the base generation and not part of the "log" directive.
 * Need to fix that.
 *
 *
 * Given a prime and a maximum number of primes; reduce default bases to multiples
 * of the bases <= maximum prime provided.
 *
 * Example: For max base-prime of 2.
 *  Prime 43 -> bases 7,            17												 ,19
 *                   /\			 /--/------\					 					  /\----------------------\
 *                  2  5	    1  5        11                                       1  5          				13
 *                     /\         /\        /\---------\                                /\                       /\-----------\
 *                    2	 3       2  3      1  3         7                              2  3                     1  5           7
 *                       /\         /\        /\        /\                                /\                       /\          /\
 *                      1  2        1 2      1  2      2  5                               1 2                     2  3        2  5
 *                                                        /\                                                         /\          /\
 *                                                       2  3                                                       1  2        2  3
 *                                                          /\                                                                     /\
 *                                                         1  2                                                                   1  2
 *
 * so result would be: 2(x3) 1(x1),  1(x5) 2(x6),      1(x5) 2(x7)
 *          which reduces to:  1(x11), 2(x16)   =>  1x11 + 2x16 = 43
 */
@Log
public class BaseReduceNPrime extends AbstractPrimeBaseGenerator
{
	static final Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));

	@Min(2)
	@Max(3)
	private int maxReduce;

	public BaseReduceNPrime(@NonNull PrimeSourceIntfc ps)
	{
		super(ps,log);
	}

	public void setMaxReduce(@Min(2) @Max(3) int maxReduce)
	{
		this.maxReduce = maxReduce;
	}

	/**
	 * Non-recursive algo to go through bases of bases to count those that we want counts for.
	 *
	 * @param primeRef cur prime being reduced
	 */
	private void primeReduction(@NonNull PrimeRefIntfc primeRef, @NonNull List<Integer> outputCntALst)
	{
		Deque<Integer> q = new ArrayDeque<>();

		// want to process initial bases for prime
		primeRef.getPrimeBaseData().getPrimeBaseIdxs(BaseTypes.DEFAULT).get(0).stream().boxed().forEach(q::add);

		while (true)
		{
			var integerIt = q.iterator();
			if (integerIt.hasNext())
			{
				// Get next base to process it and remove from deque
				// high bases of current base get added back to deqeue.
				var i = integerIt.next();
				integerIt.remove();

				  ps.getPrimeRef(i)
					.get()
					.getPrimeBaseData()
					.getPrimeBaseIdxs(BaseTypes.DEFAULT)
					.get(0)
					.stream()
					.boxed() // index integers
					.<Integer>mapMulti(
							(ii, consumer) ->
										{
											if (ii < maxReduce)
											{
												// found a non-high base so process it
												consumer.accept(ii);
											}
											else
											{
												// base is high so add it to deque for later processing
												// by outer loop
												q.add(ii);
											}
										}
								)
					.forEach(
							idx ->
									// Process the low-bases; Increment the appropriate target base count
									outputCntALst.set(idx, outputCntALst.get(idx)+1)
							);
			}
			else
				break;
		}
	}

	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	public void genBases()
	{
		if (doLog)
		{
			log.entering("BaseReduceNPrime", "genBases()");

			if (log.isLoggable(Level.INFO))
			{
				log.info(String.format("genBases(): maxReduce[%d]", maxReduce));
			}
		}

		Iterator<PrimeRefIntfc> primeIt = ps.getPrimeRefIter();
		while (primeIt.hasNext())
		{
			PrimeRefIntfc curPrime = primeIt.next();
			try
			{
				ArrayList<Integer> countForBaseIdx = new ArrayList<>(maxReduce);
				for (int i=0; i < maxReduce; i++)
					countForBaseIdx.add(i, 0);

				primeReduction(curPrime,countForBaseIdx);

				int [] tmpI = {0};
				if (doLog && log.isLoggable(Level.INFO))
				{
					log.info(String.format("Prime [%d] idx[%d] %s",
							curPrime.getPrime(),
							curPrime.getPrimeRefIdx(),
							countForBaseIdx.stream()
								.map(countForBasePrime -> String.format("base-prime-%d-count:[%d]", ps.getPrime(tmpI[0]++).get(), countForBasePrime))
								.collect(Collectors.joining(", "))));
				}
				var bs = new BitSet();
				countForBaseIdx.stream().forEach(bs::set);
				curPrime.getPrimeBaseData().addPrimeBase(bs, BaseTypes.NPRIME);
			}
			catch(Exception e)
			{
				log.severe("Error: " + e);
				break;
			}
		}
	}
}
