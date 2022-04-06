package com.starcases.prime.base.nprime;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.logging.Logger;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

/**
 * Given a Prime and a maximum number of primes; reduce default bases to multiples
 * of the bases <= maximum Prime provided.
 *
 * Example: For max base-Prime of 2.
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
public class BaseReduceNPrime extends AbstractPrimeBaseGenerator
{
	private static final Logger log = Logger.getLogger(BaseReduceNPrime.class.getName());

	@Min(2)
	@Max(3)
	private BigInteger maxReduce;

	public BaseReduceNPrime(@NonNull PrimeSourceIntfc ps)
	{
		super(ps);
	}

	public void setMaxReduce(@Min(2) @Max(3) int maxReduce)
	{
		this.maxReduce = BigInteger.valueOf(maxReduce);
	}

	/**
	 * Non-recursive algo to go through bases of bases to count those that we want counts for.
	 *
	 * @param primeRef cur Prime being reduced
	 */
	private void primeReduction(@NonNull PrimeRefIntfc primeRef, @NonNull HashSet<BigInteger> retBases, @NonNull int [] retOutputIdxCount)
	{
		// Experimentation for this use case indicate that Concurrent... perform slightly better than LinkedBlocking.. varieties of the collections.
		final var q = new ConcurrentLinkedQueue<Set<BigInteger>>();

		// want to process initial bases for Prime
		final var initialBases = primeRef.getPrimeBaseData().getPrimeBases(BaseTypes.DEFAULT).get(0);
		q.add(initialBases);

		final List<BigInteger> remainTargetBases = ps.getPrimeRefStream(false).map(PrimeRefIntfc::getPrime).takeWhile(bi -> bi.compareTo(maxReduce) < 0).collect(Collectors.toList());

		while (!q.isEmpty())
		{
			final var curBases = q.remove();
			curBases.stream().forEach(bi ->
				{
					if (bi.compareTo(maxReduce) >= 0)
					{
						ps
						.getPrimeRef(bi)
						.ifPresent(ii ->
							q.add(
									ii.getPrimeBaseData()
									.getPrimeBases(BaseTypes.DEFAULT)
									.get(0)
								));
					}
					else
					{
						retOutputIdxCount[bi.intValue()]++;
					}
				} );

			// track which bases were encountered (but only need to track "which one" on first encounter)
			if (remainTargetBases.removeAll(curBases))
			{
				final var tmpBS = curBases.stream().filter(i -> i.compareTo(maxReduce) < 0).toList();
				retBases.addAll(tmpBS);
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
			.filter(pr -> pr.getPrimeBaseData().getPrimeBases() != null && !pr.getPrimeBaseData().getPrimeBases().isEmpty())
				.forEach( curPrime ->
		{
			try
			{
				int [] retCountForBases = new int[maxReduce.intValue() +1];
				var retBases = new HashSet<BigInteger>();
				primeReduction(curPrime, retBases, retCountForBases);

				curPrime
					.getPrimeBaseData()
					.addPrimeBases(BaseTypes.NPRIME, retBases, new NPrimeBaseMetadata(retCountForBases));
			}
			catch(Exception e)
			{
				log.severe("Error: " + e);
				e.printStackTrace();

			}
		});
	}
}
