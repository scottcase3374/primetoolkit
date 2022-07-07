package com.starcases.prime.base.nprime;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.bag.immutable.primitive.ImmutableLongBagFactoryImpl;
import org.eclipse.collections.impl.bag.mutable.primitive.MutableLongBagFactoryImpl;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;

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
	@Setter
	@Min(2)
	@Max(3)
	private long maxReduce;

	/**
	 * constructor for Base NPrime construction
	 * @param primeSrc
	 */
	public BaseReduceNPrime(@NonNull final PrimeSourceIntfc primeSrc)
	{
		super(primeSrc);
	}

	/**
	 * fluent style setter for the max reduction value
	 * @param maxReduce
	 * @return
	 */
	public BaseReduceNPrime assignMaxReduce(final long maxReduce)
	{
		this.maxReduce = maxReduce;
		return this;
	}

	/**
	 * Non-recursive algo to go through bases of bases to
	 * count those that we want counts for.
	 *
	 * @param primeRef cur Prime being reduced
	 */
	@SuppressWarnings({"PMD.LawOfDemeter"})
	private void primeReduction(@NonNull final PrimeRefIntfc primeRef, @NonNull final MutableLongCollection retBases)
	{
		// want to process initial bases for Prime
		MutableLongCollection bases = primeRef
				.getPrimeBaseData()
				.getPrimeBases()
				.stream()
				.map(ImmutableLongCollection::longIterator)
				.collect(null)
				;

		while (!bases.isEmpty())
		{
			bases.select( bi -> ( bi - maxReduce) < 0 ,retBases);

			final MutableLongCollection tmp = bases
					.select(bi ->  (bi - maxReduce) >= 0)
					/*.collect(bi1 -> primeSrc.getPrimeRefByPrime(bi1)
								.orElseThrow()
								.getPrimeBaseData()
								.getPrimeBases()) */

					//.flatCollect(Collection::stream)	// List of Set of BigInteger  -> Stream of Set of BigInteger
					//.flatMap(Collection::stream)	// Stream of Set of BigInteger to Stream of BigInteger
					//.collect(Collectors2.toList())	// Stream of BigIntegers -> List of BigInteger
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
					final MutableLongCollection retBases = MutableLongBagFactoryImpl.INSTANCE.empty();
					primeReduction(curPrime, retBases);
					final MutableList<ImmutableLongCollection> lst = MutableListFactoryImpl.INSTANCE.of(retBases.toImmutable(), ImmutableLongBagFactoryImpl.INSTANCE.with(curPrime.getPrime()));

					curPrime
						.getPrimeBaseData()
						.addPrimeBases(BaseTypes.NPRIME, lst, new NPrimeBaseMetadata(retBases.toImmutable()));
				});
	}

	private boolean isNonNullEmpty(final MutableCollection<?> coll)
	{
		return coll != null && !coll.isEmpty();
	}
}
