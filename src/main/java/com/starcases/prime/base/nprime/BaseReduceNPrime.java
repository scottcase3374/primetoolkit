package com.starcases.prime.base.nprime;

import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

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
	static volatile byte[] intersectBytes;

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

		// Normally wouldn't set a static like this but
		// this call is only made once per jvm invocation
		// as part of the initialization
		intersectBytes = new byte[1];
		intersectBytes[0] = (byte)Math.round(Math.pow(2.0d, maxReduce)-1); // set to bit representing the range of indexes to reduce using.
	}

	/**
	 * Non-recursive algo to go through bases of bases to count those that we want counts for.
	 *
	 * @param primeRef cur prefixPrime being reduced
	 */
	private void primeReduction(@NonNull PrimeRefIntfc primeRef, @NonNull BitSet retBaseIdxs, @NonNull int [] retOutputIdxCount)
	{
		// Experimentation for this use case indicate that Concurrent... perform slightly better than LinkedBlocking.. varieties of the collections.
		final var q = new ConcurrentLinkedQueue<BitSet>();

		// want to process initial bases for prefixPrime
		final var bs = primeRef.getPrimeBaseData().getPrimeBaseIdxs(BaseTypes.DEFAULT).get(0);
		q.add(bs);

		//  Method determined through experimentation between:
		//   get(0,maxReduce) of static bitset
		//   clone() of static bitset
		//   static byte array with Bitset.valueOf()
		// - Note the use of 'volatile' on intersectBytes, resulted in reduced run times. Appears reduced memory contention.
		//		For this use, bytes are "read-only" once set so volatile ensures visibility between threads without
		//      providing any mutual exclusion which is fine since we only need to read the value.
		final BitSet intersect = BitSet.valueOf(intersectBytes);

		while (!q.isEmpty())
		{
			final var bsCur = q.remove();
			bsCur.stream().boxed().forEach(i ->
				{
					if (i >= maxReduce)
					{
						q.add(ps.getPrimeRef(i).get().getPrimeBaseData().getPrimeBaseIdxs(BaseTypes.DEFAULT).get(0));
					}
					else
					{
						retOutputIdxCount[i]++;
					}
				} );

			// track which indexes were encountered (but only need to track "which one" on first encounter)
			if (intersect.intersects(bsCur))
			{
				final var tmpBS = bsCur.get(0, maxReduce);
				retBaseIdxs.or(tmpBS);
				intersect.andNot(tmpBS);
			}
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
