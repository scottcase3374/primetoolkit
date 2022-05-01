package com.starcases.prime.base.nprime;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.list.mutable.MultiReaderFastList;

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
	private void primeReduction(@NonNull PrimeRefIntfc primeRef, @NonNull MutableSortedBag<BigInteger> retBases)
	{
		// want to process initial bases for Prime
		final var initialBases = primeRef.getPrimeBaseData().getPrimeBases(BaseTypes.DEFAULT).get(0);
		final List<Set<BigInteger>> bases = MultiReaderFastList.newList();
		bases.add(initialBases);

		while (!bases.isEmpty())
		{
			final var curBases = bases.remove(bases.size()-1);
			curBases.forEach(bi ->
				{
					if (bi.compareTo(maxReduce) >= 0)
					{
						ps
						.getPrimeRef(bi)
						.ifPresent(ii ->
							bases.add(
									ii.getPrimeBaseData()
									.getPrimeBases(BaseTypes.DEFAULT)
									.get(0)
								));
					}
					else
					{
						retBases.add(bi);
					}
				} );
		}
	}

	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	@Override
	protected void genBasesImpl()
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
				MutableSortedBag<BigInteger> retBases = TreeBag.newBag();
				primeReduction(curPrime, retBases);

				curPrime
					.getPrimeBaseData()
					.addPrimeBases(BaseTypes.NPRIME, List.of(retBases.toSortedSet()), new NPrimeBaseMetadata(retBases));
			}
			catch(Exception e)
			{
				log.severe("Error: " + e);
				e.printStackTrace();

			}
		});
	}
}
