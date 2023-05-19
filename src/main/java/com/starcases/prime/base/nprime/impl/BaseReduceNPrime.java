package com.starcases.prime.base.nprime.impl;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.eclipse.collections.api.bag.primitive.MutableLongBag;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.bag.immutable.primitive.ImmutableLongBagFactoryImpl;
import org.eclipse.collections.impl.bag.mutable.primitive.MutableLongBagFactoryImpl;
import org.eclipse.collections.impl.list.mutable.MutableListFactoryImpl;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.impl.PrimeBaseGenerator;
import com.starcases.prime.core.api.PrimeRefIntfc;

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
public class BaseReduceNPrime extends PrimeBaseGenerator
{
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
	public BaseReduceNPrime(@NonNull final BaseTypes baseType)
	{
		super(baseType);
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
	 * Non-recursive algo to subdivide bases into sub-bases and
	 * count those in our desired range upto the "reduce to <base>" value selected.
	 *
	 * @param primeRef cur Prime being reduced
	 */
	private void primeReduction(@NonNull final PrimeRefIntfc primeRef, @NonNull final MutableLongCollection retBases)
	{
		// want to process initial bases for Prime

		MutableLongCollection bases = MutableLongBagFactoryImpl.INSTANCE.empty();
		MutableLongCollection subBases = MutableLongBagFactoryImpl.INSTANCE.empty();

		primeRef
			.getPrimeBaseData()
			.getPrimeBases()
			.flatCollectLong(c -> c, bases)
			;

		while (!bases.isEmpty())
		{
			// Keep the desired values
			bases.select( bi -> ( bi - maxReduce) < 0 ,retBases);
			bases.removeIf(bi -> ( bi - maxReduce) < 0);

			final MutableLongCollection tmpSubBases = subBases;
			// process larger bases into a set of smaller bases for further processing
			bases.forEach(
				l ->
					primeSrc
						.getPrimeRefForPrime(l)
						.ifPresent(b1 ->
								b1
								.getPrimeBaseData()
								.getPrimeBases()
								.flatCollectLong(c1 -> c1, tmpSubBases)
						  ));

			// Reuse storage instead of new collection allocations
			final MutableLongCollection tmpBases = bases;
			bases = subBases;
			subBases = tmpBases;
			subBases.clear();
		}
	}

	@Override
	public void genBasesForPrimeRef(final PrimeRefIntfc curPrime)
	{
		final MutableLongBag retBases = MutableLongBagFactoryImpl.INSTANCE.empty();
		primeReduction(curPrime, retBases);
		final MutableList<ImmutableLongCollection> lst = MutableListFactoryImpl.INSTANCE.of(retBases.toImmutable(), ImmutableLongBagFactoryImpl.INSTANCE.with(curPrime.getPrime()));
		final NPrimeBaseMetadata npbmd = new NPrimeBaseMetadata(retBases.toImmutable());

		curPrime
			.getPrimeBaseData()
			.addPrimeBases(BaseTypes.NPRIME, lst, npbmd);
	}
}
