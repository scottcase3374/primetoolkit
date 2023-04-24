package com.starcases.prime.base.triples_impl;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;

import org.eclipse.collections.impl.factory.Sets;

import com.starcases.prime.base_api.BaseTypes;
import com.starcases.prime.core_api.PrimeRefIntfc;
import com.starcases.prime.core_api.PrimeSourceIntfc;

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
public class AllTriples
{
	/**
	 * prime source ref for lookup of prime/prime refs.
	 */
	@NonNull
	@Getter(AccessLevel.PRIVATE)
	private final PrimeSourceIntfc primeSrc;

	/**
	 * at least 1 non-null component
	 */
	private final Predicate<PrimeRefIntfc[]> partialTriple =
			prefArray -> Arrays.stream(prefArray).anyMatch(Objects::nonNull);

	/**
	 * no components null
	 */
	private final Predicate<PrimeRefIntfc[]> nonNullTriple =
			prefArray -> Arrays.stream(prefArray).allMatch(Objects::nonNull);

	private final Function<PrimeRefIntfc[], Long> sumTriple =
			prefArray -> Arrays.stream(prefArray).collect(Collectors.summingLong(p -> p.getPrime()));

	/**
	 * constructor for creating base type of "triples".
	 * @param primeSrc
	 * @param targetPrime
	 */
	public AllTriples(@NonNull final PrimeSourceIntfc primeSrc, final boolean preferParallel)
	{
		this.primeSrc = primeSrc;
	}

	private void incrementIndices(final long [] indices)
	{
		if (indices[TripleMember.BOT.ordinal()]+1 < indices[TripleMember.MID.ordinal()])
		{
			indices[TripleMember.BOT.ordinal()]++;
		}
		else if (indices[TripleMember.MID.ordinal()]+1 < indices[TripleMember.TOP.ordinal()])
		{
			indices[TripleMember.BOT.ordinal()] = 0;
			indices[TripleMember.MID.ordinal()]++;
		}
		else
		{
			indices[TripleMember.BOT.ordinal()] = 0;
			indices[TripleMember.MID.ordinal()] = 1;
			indices[TripleMember.TOP.ordinal()]++;
		}
	}

	private Stream<PrimeRefIntfc[]> tripleStream()
	{
		final long [] indices = {0,1,2};
		final PrimeRefIntfc [] triple = {null, null, null};

		return Stream.generate(
				() ->
				{
					incrementIndices(indices);
					Arrays.stream(TripleMember.values())
					.forEach( memberIdx ->
							{
								try
								{
									triple[memberIdx.ordinal()] = primeSrc.getPrimeRefForIdx(indices[memberIdx.ordinal()]).orElseThrow();
								}
								catch(Exception e)
								{
									triple[memberIdx.ordinal()] = null;
								}
							}
						);
					return triple;
				}
			);
	}



	/**
	 * Main entry point to this processing - produce all viable triples and add each to the corresponding prime base.

	 * This is a "mostly brute force" method which is shown by pretty slow performance.
	 */
	public void process()
	{
		tripleStream()
			.filter(partialTriple)
			.takeWhile(nonNullTriple)
			.forEach(triple ->

							primeSrc
								.getPrimeRefForPrime(() -> sumTriple.apply(triple))
								.ifPresent(prim -> addPrimeBases(prim, triple))

					);
	}

	private void addPrimeBases(final @NonNull PrimeRefIntfc prime, final @NonNull PrimeRefIntfc [] triple)
	{
		final ImmutableLongCollection primeBase = Sets.immutable.of(triple).collectLong(PrimeRefIntfc::getPrime);
		prime.getPrimeBaseData().addPrimeBases(primeBase, BaseTypes.THREETRIPLE);
	}
}
