package com.starcases.prime.base.nprime;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.collector.Collectors2;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
@SuppressWarnings({"PMD.CommentSize", "PMD.LawOfDemeter"})
public class BaseReduceNPrime extends AbstractPrimeBaseGenerator
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(BaseReduceNPrime.class.getName());

	/**
	 * Reduce all values greater than maxReduce - track multiples
	 */
	@Getter(AccessLevel.PUBLIC)
	@Setter(AccessLevel.PUBLIC)
	@Min(2)
	@Max(3)
	private BigInteger maxReduce;

	/**
	 * constructor for Base NPrime construction
	 * @param primeSrc
	 */
	public BaseReduceNPrime(@NonNull final PrimeSourceIntfc primeSrc)
	{
		super(primeSrc);
	}

	/**
	 * Non-recursive algo to go through bases of bases to
	 * count those that we want counts for.
	 *
	 * @param primeRef cur Prime being reduced
	 */
	@SuppressWarnings({"PMD.LawOfDemeter"})
	private void primeReduction(@NonNull final PrimeRefIntfc primeRef, @NonNull final MutableSortedBag<BigInteger> retBases)
	{
		// want to process initial bases for Prime
		MutableList<BigInteger> bases = primeRef
				.getPrimeBaseData()
				.getPrimeBases()
				.stream()
				.flatMap(Set::stream)
				.collect(Collectors2.toList());

		while (!bases.isEmpty())
		{
			bases.selectWith((BigInteger bi, BigInteger reduceVal) -> bi.compareTo(reduceVal) < 0, maxReduce, retBases);

			final var tmp = bases
					.stream()
					.filter(bi ->  bi.compareTo(maxReduce) >= 0)
					.map(bi1 -> primeSrc.getPrimeRef(bi1)
								.orElseThrow()
								.getPrimeBaseData()
								.getPrimeBases())
					.flatMap(Collection::stream)	// List of Set of BigInteger  -> Stream of Set of BigInteger
					.flatMap(Collection::stream)	// Stream of Set of BigInteger to Stream of BigInteger
					.collect(Collectors2.toList())	// Stream of BigIntegers -> List of BigInteger
					;
			bases = tmp;
		}

	}

	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	@Override
	@SuppressWarnings("PMD.LawOfDemeter")
	protected void genBasesImpl()
	{
		if (isBaseGenerationOutput())
		{
			LOG.entering("BaseReduceNPrime", "genBases()");

			if (LOG.isLoggable(Level.INFO))
			{
				LOG.info(String.format("genBases(): maxReduce[%d]", maxReduce));
			}
		}

		primeSrc.getPrimeRefStream(this.preferParallel)
			.filter(pr -> isNonNullEmpty(pr.getPrimeBaseData().getPrimeBases()))
				.forEach( curPrime ->
		{
			try
			{
				final MutableSortedBag<BigInteger> retBases = TreeBag.newBag();
				primeReduction(curPrime, retBases);

				curPrime
					.getPrimeBaseData()
					.addPrimeBases(BaseTypes.NPRIME, List.of(retBases.toSortedSet(), Set.of(curPrime.getPrime())), new NPrimeBaseMetadata(retBases));
			}
			catch(Exception e)
			{
				if (LOG.isLoggable(Level.SEVERE))
				{
					LOG.severe("Error: " + e);
				}
			}
		});
	}

	private boolean isNonNullEmpty(final Collection<?> coll)
	{
		return coll != null && !coll.isEmpty();
	}
}
