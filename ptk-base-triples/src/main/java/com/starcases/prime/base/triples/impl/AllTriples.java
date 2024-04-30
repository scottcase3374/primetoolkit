package com.starcases.prime.base.triples.impl;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import java.util.stream.Collectors;

import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

enum TripleMember
{
	/**
	 * Index for bottom item
	 */
	BOT,

	/**
	 * index for middle item
	 */
    MID,

    /**
     * index for top item
     */
	TOP
	;
}

/**
 *
 * Class implementing the logic for finding all viable triples.
 *
 * Sum combinations of 3 primes and if result is a (pre-existing) prime
 * then add the 3 primes a "base". Current method works fine when starting
 * from low primes and working higher but would be very inefficient if
 * bases already existed for low primes and you want to add bases for
 * new primes that were not handled at an earlier time.
 *
 */
public class AllTriples implements Runnable
{
	private final PrimeRefFactoryIntfc primeRef;
	private long prime;
	private final BaseReduceTriple baseReduce;

	private long topPrimeInit; 		// 3/6
	private long bottomPrimeInit; 	// 1/6
	private long midPrimeInit;	    // 2/6

	private PrimeRefFactoryIntfc topPRefInit;
	private PrimeRefFactoryIntfc midPRefInit;
	private PrimeRefFactoryIntfc bottomPRefInit;

	/**
	 * prime source ref for lookup of prime/prime refs.
	 */
	@NonNull
	@Getter(AccessLevel.PRIVATE)
	private final PrimeSourceIntfc primeSrc;

	/**
	 * no components null
	 */
	private final Predicate<PrimeRefIntfc[]> nonNullTriple =
			prefArray -> Arrays.stream(prefArray).allMatch(Objects::nonNull);

	/**
	 * sum up the set of 3 primes.
	 */
	private final Function<PrimeRefIntfc[], Long> sumTriple =
			prefArray -> Arrays.stream(prefArray).collect(Collectors.summingLong(p -> p.getPrime()));

	/**
	 * constructor for creating base type of "triples".
	 *
	 * package visibility due to service provider provisioning service.
	 *
	 * @param primeSrc
	 * @param targetPrime
	 */
	AllTriples(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final PrimeRefFactoryIntfc primeRef, @NonNull final BaseReduceTriple baseReduce)
	{
		this.primeSrc = primeSrc;
		this.primeRef = primeRef;
		this.prime = primeRef.getPrime();
		this.baseReduce = baseReduce;
	}

	private boolean decrementIndices(final PrimeRefFactoryIntfc [] indices)
	{
		boolean done = false;

		if (indices[TripleMember.BOT.ordinal()].getPrimeRefIdx()-1 > 0)
		{
			// Adjust bottom
			indices[TripleMember.BOT.ordinal()] = (PrimeRefFactoryIntfc)indices[TripleMember.BOT.ordinal()].getPrevPrimeRef().get();
		}
		else if (indices[TripleMember.MID.ordinal()].getPrimeRefIdx()-1 > 1)
		{
			// adjust mid
			indices[TripleMember.MID.ordinal()] = (PrimeRefFactoryIntfc)indices[TripleMember.MID.ordinal()].getPrevPrimeRef().get();

			// reset bottom to mid-1
			indices[TripleMember.BOT.ordinal()] = (PrimeRefFactoryIntfc)indices[TripleMember.MID.ordinal()].getPrevPrimeRef().get();

		}
		else if (indices[TripleMember.TOP.ordinal()].getPrimeRefIdx()-1 > 3)
		{
			// adjust top
			indices[TripleMember.TOP.ordinal()] = (PrimeRefFactoryIntfc)indices[TripleMember.TOP.ordinal()].getPrevPrimeRef().get();

			// adjust mid to top-1
			indices[TripleMember.MID.ordinal()] = (PrimeRefFactoryIntfc)indices[TripleMember.TOP.ordinal()].getPrevPrimeRef().get();

			// adjust bot to mid-1
			indices[TripleMember.BOT.ordinal()] = (PrimeRefFactoryIntfc)indices[TripleMember.MID.ordinal()].getPrevPrimeRef().get();
		}
		else
		{
			done = true;
		}

		return done;
	}

	private boolean incrementIndices(final PrimeRefFactoryIntfc [] indices, @NonNull PrimeRefFactoryIntfc prime)
	{
		boolean done = false;
		if (indices[TripleMember.BOT.ordinal()].getPrimeRefIdx()+1 < indices[TripleMember.MID.ordinal()].getPrimeRefIdx())
		{
			indices[TripleMember.BOT.ordinal()] = (PrimeRefFactoryIntfc)indices[TripleMember.BOT.ordinal()].getNextPrimeRef().get();
		}
		else if (indices[TripleMember.MID.ordinal()].getPrimeRefIdx()+1 < indices[TripleMember.TOP.ordinal()].getPrimeRefIdx())
		{
			indices[TripleMember.MID.ordinal()] = (PrimeRefFactoryIntfc)indices[TripleMember.MID.ordinal()].getNextPrimeRef().get();
			indices[TripleMember.BOT.ordinal()] = (PrimeRefFactoryIntfc)primeSrc.getPrimeRefForIdx(0).get();
		}
		else if (indices[TripleMember.TOP.ordinal()].getPrimeRefIdx()+1 < prime.getPrimeRefIdx())
		{
			indices[TripleMember.TOP.ordinal()] = (PrimeRefFactoryIntfc)indices[TripleMember.TOP.ordinal()].getNextPrimeRef().get();
			indices[TripleMember.BOT.ordinal()] = (PrimeRefFactoryIntfc)primeSrc.getPrimeRefForIdx(0).get();
			indices[TripleMember.MID.ordinal()] = (PrimeRefFactoryIntfc)indices[TripleMember.BOT.ordinal()].getNextPrimeRef().get();
		}
		else
		{
			done = true;
		}

		return done;
	}

	@Override
	public void run()
	{
		boolean incDone = prime < 11;
		boolean decDone = prime < 11;

		this.topPrimeInit = Math.max((int)Math.ceil(prime / 2), 1);
		this.bottomPrimeInit = Math.max((int)Math.ceil(prime / 6), 1);
		this.midPrimeInit = Math.max(topPrimeInit - bottomPrimeInit, 1);

		this.topPRefInit = (PrimeRefFactoryIntfc)primeSrc.getPrimeRefCeiling(topPrimeInit).get();
		this.midPRefInit = (PrimeRefFactoryIntfc)primeSrc.getPrimeRefCeiling(midPrimeInit).get();
		this.bottomPRefInit = (PrimeRefFactoryIntfc)primeSrc.getPrimeRefCeiling(bottomPrimeInit).get();

		final PrimeRefFactoryIntfc [] incIndiceRefs = {
							bottomPRefInit,
							midPRefInit,
							topPRefInit
						};

		final PrimeRefFactoryIntfc [] decIndiceRefs = {
				bottomPRefInit,
				midPRefInit,
				topPRefInit
			};

		int rounds = 0;
		boolean found = false;
		while (!(incDone && decDone) && !found)
		{
			rounds++;
			if (!decDone && sumTriple.apply(decIndiceRefs) == prime)
			{
				baseReduce.addPrimeBases(primeRef, decIndiceRefs);
				found = true;
			}
			else if (!incDone && sumTriple.apply(incIndiceRefs) == prime)
			{
				baseReduce.addPrimeBases(primeRef, incIndiceRefs);
				found = true;
			}
			else
			{
				if (!decDone)
				{
					decDone = decrementIndices(decIndiceRefs);
				}
				if (!incDone)
				{
					incDone = incrementIndices(incIndiceRefs, primeRef);
				}
			}
		}
		if (prime >= 11 && !found)
		{
			System.out.println(String.format("##not-found %b prime-idx %d prime %d rounds %d decDone: %b incDone: %b",
					found,
					primeRef.getPrimeRefIdx(),
					primeRef.getPrime(),
					rounds,
					decDone,
					incDone));
		}

	}
}
