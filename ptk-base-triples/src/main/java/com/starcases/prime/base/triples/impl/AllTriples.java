package com.starcases.prime.base.triples.impl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import java.util.function.Function;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;

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
 * Sum combinations of 3 primes and if result is a (pre-existing) prime
 * then add the 3 primes a "base". Current method works fine when starting
 * from low primes and working higher but would be very inefficient if
 * bases already existed for low primes and you want to add bases for
 * new primes that were not handled at an earlier time.
 *
 */
public class AllTriples implements Runnable
{
	private static final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();

	public static AtomicInteger incFound = new AtomicInteger(0);
	public static AtomicInteger decFound = new AtomicInteger(0);

	private final PrimeRefFactoryIntfc primeRef;
	private final BaseReduceTriple baseReduce;

	/**
	 * prime source ref for lookup of prime/prime refs.
	 */
	@NonNull
	@Getter(AccessLevel.PRIVATE)
	private final PrimeSourceIntfc primeSrc;

	/**
	 * sum up the set of 3 primes.
	 */
	private final Function<PrimeRefIntfc[], Long> sumTriple =
			prefArray -> (Long) Arrays.stream(prefArray).mapToLong(PrimeRefIntfc::getPrime).sum();

	/**
	 * constructor for creating base type of "triples".
	 * Package visibility due to service provider provisioning service.
	 *
	 * @param primeSrc PrimeSource reference.
	 * @param primeRef The target prime ref.
	 */
	AllTriples(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final PrimeRefFactoryIntfc primeRef, @NonNull final BaseReduceTriple baseReduce)
	{
		this.primeSrc = primeSrc;
		this.primeRef = primeRef;
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
		final long prime = primeRef.getPrime();

		boolean incDone = prime < 11;
		boolean decDone = prime < 11;

		final long topPrimeInit = Math.max((int)Math.floor(prime / 2.0), 1);
		final long bottomPrimeInit = Math.max((int)Math.ceil(prime / 6.0), 1);
		final long midPrimeInit = Math.max(topPrimeInit - bottomPrimeInit, 1);

		final PrimeRefFactoryIntfc topPRefInit = (PrimeRefFactoryIntfc)primeSrc.getPrimeRefCeiling(topPrimeInit).get();
		final PrimeRefFactoryIntfc midPRefInit = (PrimeRefFactoryIntfc)primeSrc.getPrimeRefCeiling(midPrimeInit).get();
		final PrimeRefFactoryIntfc bottomPRefInit = (PrimeRefFactoryIntfc)primeSrc.getPrimeRefCeiling(bottomPrimeInit).get();

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

		int incRounds = 0;
		int decRounds = 0;
		boolean found = false;
		while (!(incDone && decDone) && !found)
		{
			if (!incDone && sumTriple.apply(incIndiceRefs) == prime)
			{
				baseReduce.addPrimeBases(primeRef, incIndiceRefs);
				found = true;
				incFound.incrementAndGet();
				incRounds++;
			}
			else if (!decDone && sumTriple.apply(decIndiceRefs) == prime)
			{
				baseReduce.addPrimeBases(primeRef, decIndiceRefs);
				found = true;
				decFound.incrementAndGet();
				decRounds++;
			}
			else
			{
				if (!incDone)
				{
					incDone = incrementIndices(incIndiceRefs, primeRef);
				}
				if (!decDone)
				{
					decDone = decrementIndices(decIndiceRefs);
				}
			}
		}

		if (primeRef.getPrimeRefIdx() % 1_000 == 0)
		{
			statusHandler.output(String.format("TRIPLE idxtoprime idx: %d inc-rounds: %d  dec-rounds: %d inc: %d  dec: %d",
					primeRef.getPrimeRefIdx(),
					incRounds,
					decRounds,
					incFound.get(),
					decFound.get()));
		}

		if (prime >= 11 && !found)
		{
			statusHandler.errorOutput(String.format("##TRIPLE not-found %b prime-idx %d prime %d inc-rounds: %d dec-rounds: %d decDone: %b incDone: %b",
					found,
					primeRef.getPrimeRefIdx(),
					primeRef.getPrime(),
					incRounds,
					decRounds,
					decDone,
					incDone));
		}
	}
}
