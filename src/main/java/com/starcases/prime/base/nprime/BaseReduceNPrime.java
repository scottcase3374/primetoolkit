package com.starcases.prime.base.nprime;

import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;

/**
 * Given a prefixPrime and a maximum number of primes; reduce default bases to multiples
 * of the bases <= maximum prefixPrime provided.
 *
 * Example: For max base-prefixPrime of 2.
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
	@Min(2)
	@Max(3)
	private int maxReduce;

	public BaseReduceNPrime(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	public void setMaxReduce(@Min(2) @Max(3) int maxReduce)
	{
		this.maxReduce = maxReduce;
	}

	/**
	 * Non-recursive algo to go through bases of bases to count those that we want counts for.
	 *
	 * @param primeRef cur prefixPrime being reduced
	 */
	private void primeReduction(@NonNull PrimeRefIntfc primeRef, @NonNull BitSet retBaseIdxs, @NonNull int [] retOutputIdxCount)
	{
		// want to process initial bases for prefixPrime
		final var q = primeRef.getPrimeBaseData().getPrimeBaseIdxs(BaseTypes.DEFAULT).get(0).stream().boxed().collect(Collectors.toCollection(ConcurrentLinkedDeque::new));

		while (true)
		{
			var integerIt = q.iterator();
			if (integerIt.hasNext())
			{
				// Get next base to process it and remove from "queue"
				// high bases of current base get added back to "queue"
				var i = integerIt.next();
				integerIt.remove();

				ps.getPrimeRef(i).ifPresent( p->

							   p.getPrimeBaseData()
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
												{ retOutputIdxCount[idx]++; retBaseIdxs.set(idx); }
										)
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

		ps.getPrimeRefStream(this.preferParallel)
				.forEach( curPrime ->
		{
			try
			{
				int [] retCountForBaseIdx = new int[maxReduce];
				var retBaseIdxs = new BitSet();
				primeReduction(curPrime, retBaseIdxs, retCountForBaseIdx);

				curPrime.getPrimeBaseData().addPrimeBase(BaseTypes.NPRIME, retBaseIdxs, new NPrimeBaseMetadata(retCountForBaseIdx));
			}
			catch(Exception e)
			{
				log.severe("Error: " + e);

			}
		});
	}
}
