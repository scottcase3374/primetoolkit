package com.starcases.prime.base.triples;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 *
 * Indicator to relationship between current sum of triple values vs the target Prime.
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
enum SumConstraintState
{
	/**
	 * Indicate sum is not a Prime value and/or doesn't match the Prime. Usable as a default in most cases.
	 */
	NONMATCH(null)
	{},

	/**
	 * indicate sum is lower than target Prime
	 *
	 * (Prime - sum).signNum
	 */
	INCREMENT_SUM(1)
	{},

	/**
	 * indicate sum is higher than target Prime
	 *
	 * (Prime - sum).signNum
	 */
	DECREMENT_SUM(-1)
	{},

	/**
	 * indicate sum matches target Prime
	 *
	 * (Prime - sum).signNum
	 */
	MATCH(0)
	{};

	private final Integer compToResult;

	SumConstraintState(final Integer compToResult)
	{
		this.compToResult = compToResult;
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	static SumConstraintState getEnum(final Integer compToResult)
	{
		return Arrays.stream(SumConstraintState.values())
				.filter(e -> Objects.nonNull(e.compToResult) && e.compToResult.equals(compToResult))
				.findAny()
				.orElse(NONMATCH);
	}

	/**
	 * Determine the state of the sum-constraints
	 *
	 * @param primeRefs
	 * @param targetPrime
	 * @return
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	public static SumConstraintState checkSumConstraints(
			@NonNull final PrimeRefIntfc [] primeRefs,
			@NonNull final PrimeRefIntfc targetPrime)
	{
		final var sum = Arrays.
						stream(primeRefs)
						.filter(Objects::nonNull)
						.map(PrimeRefIntfc::getPrime)
						.reduce(BigInteger.ZERO, BigInteger::add);

		// determine if sum is higher than Prime, equal to Prime, less than Prime or just doesn't match for some reason.
		final var sumComptoPrime = targetPrime.getPrime().compareTo(sum);

		return SumConstraintState.getEnum(sumComptoPrime);
	}
}

/**
 *
 * A condition status - indicate specific issues or lack of issues with/between values.
 *
 */

enum ConditionConstraintState
{
	/**
	 * One or more of the required 3 bases are not set
	 */
	MISSING_BASE,

	/**
	 * 2 bases have the same Prime value
	 */
	DUPE,

	/**
	 * Bot/top/mid values need to maintain the relationship
	 * 	1 <= Bot < Mid < Top
	 * This flag is set if the relationship is/will be violated.
	 */
	RANGE_ERROR,

	/**
	 * No errors with values
	 */
	OK;

	/**
	 * Need to simplify still.
	 *
	 * @param primeRefs
	 * @return
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	public static ConditionConstraintState checkConditionConstraints(
			@NonNull final PrimeRefIntfc ... primeRefs)
	{
		// TripleIdx - BOT,MID,TOP
		var constraintState = ConditionConstraintState.OK;
		var baseCount = 0;
		long [] bases = { -1, -1, -1 }; // dummy values

		for (final var pr : primeRefs)
		{
			bases[baseCount] = pr.getPrimeRefIdx();
			if (bases[baseCount++] > pr.getPrimeRefIdx())
			{
				constraintState = ConditionConstraintState.RANGE_ERROR;
				break;
			}
		}

		if (ConditionConstraintState.OK.equals(constraintState))
		{
			if (baseCount != bases.length)
			{
				constraintState = ConditionConstraintState.MISSING_BASE;
			}
			else
			{
				if (Arrays.stream(bases).distinct().count() != bases.length)
				{
					constraintState = ConditionConstraintState.DUPE;
				}
			}
		}
		return constraintState;
	}
}

/**
 *
 * Class implementing the logic for finding all viable triples.
 *
 */
@SuppressWarnings({"PMD.LongVariable", "PMD.CommentSize"})
public class AllTriples
{
	/**
	 * unchanging set of constants.
	 */
	private static final EnumSet<ConditionConstraintState> BAD_CONDITION_STATE =
			EnumSet.of(ConditionConstraintState.DUPE,
					ConditionConstraintState.RANGE_ERROR,
					ConditionConstraintState.MISSING_BASE);

	/**
	 * unchanging set of constants.
	 */
	private static  final EnumSet<SumConstraintState> GOOD_SUM_STATE =
			EnumSet.of(SumConstraintState.MATCH,
					SumConstraintState.INCREMENT_SUM);

	/**
	 * Index for bottom item
	 */
	private static final  int BOT = 0;

	/**
	 * index for middle item
	 */
    private static final  int MID = 1;

    /**
     * index for top item
     */
	private static final  int TOP = 2;

	/**
	 * Target prime to equal
	 */
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private  final PrimeRefIntfc targetPrime;

	/**
	 * prime source ref for lookup of prime/prime refs.
	 */
	@NonNull
	@Getter(AccessLevel.PRIVATE)
	private final PrimeSourceIntfc primeSrc;

	/**
	 * constructor for creating base type of "triples".
	 * @param primeSrc
	 * @param targetPrime
	 */
	public AllTriples(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final PrimeRefIntfc targetPrime)
	{
		this.targetPrime = targetPrime;
		this.primeSrc = primeSrc;
	}

	/**
	 * Move to next Prime ref (of specified component of triple) and manage tracking the resulting sum/condition state.
	 *
	 * @param idx
	 * @param triple
	 * @param sumConstraint
	 * @param conditionConstraint
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	private void nextPrimeRef(
						final int idx,
						@NonNull final PrimeRefIntfc [] triple,
						@NonNull final SumConstraintState [] sumConstraint,
						@NonNull final ConditionConstraintState...conditionConstraint)
	{
		if (triple[idx] != null)
		{
			triple[idx].getNextPrimeRef().ifPresent( pr ->  triple[idx] = pr);
		}
		sumConstraint[0] = SumConstraintState.checkSumConstraints(triple, targetPrime);
		conditionConstraint[0] = ConditionConstraintState.checkConditionConstraints(triple);
	}

	/**
	 * Main entry point to this processing - which processes a single Prime to produce a list of all viable triples
	 * which individually sum to the Prime.
	 *
	 * This is a "mostly brute force" method which is shown by pretty slow performance.
	 *
	 * Note:
	 * Performing the process for each prime ref in concurrent/parallel produces a speedup but since this is
	 * generating all combinations of valid sums, it is slow for more than small sets of primes.
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	public void process()
	{
		final PrimeRefIntfc [] triple =
			{
				primeSrc.getPrimeRef(0).orElse(null),  // Prime 1
				primeSrc.getPrimeRef(2).orElse(null), // Prime 3
				primeSrc.getPrimeRef(4).orElse(null) // Prime 7
			};

		final SumConstraintState [] sumConstraint = {SumConstraintState.checkSumConstraints(triple, targetPrime)};
		final ConditionConstraintState [] conditionConstraint = {ConditionConstraintState.checkConditionConstraints(triple)};

		do // cur top
		{
			do // cur mid
			{
				do // cur bot
				{
					if (sumConstraint[0] == SumConstraintState.MATCH && !BAD_CONDITION_STATE.contains(conditionConstraint[0]))
					{
						addPrimeBases(targetPrime, triple);
						break;
					}
					this.nextPrimeRef(BOT, triple, sumConstraint, conditionConstraint);
				}
				while(GOOD_SUM_STATE.contains(sumConstraint[0]) && !BAD_CONDITION_STATE.contains(conditionConstraint[0]));

				// reset inner loop
				triple[BOT] =  primeSrc.getPrimeRef(0).orElse(null);

				this.nextPrimeRef(MID, triple, sumConstraint, conditionConstraint);
			}
			while(GOOD_SUM_STATE.contains(sumConstraint[0]) && !BAD_CONDITION_STATE.contains(conditionConstraint[0]));

			// reset inner loops
			triple[MID] = primeSrc.getPrimeRef(1).orElse(null);
			triple[BOT] = primeSrc.getPrimeRef(0).orElse(null);

			this.nextPrimeRef(TOP, triple, sumConstraint, conditionConstraint);
		}
		while(GOOD_SUM_STATE.contains(sumConstraint[0]) && !BAD_CONDITION_STATE.contains(conditionConstraint[0]));
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	private void addPrimeBases(final @NonNull PrimeRefIntfc prime, final @NonNull PrimeRefIntfc [] vals)
	{
		final var basePrimeColl = Arrays.stream(vals)
			.filter(Objects::nonNull)
			.map(PrimeRefIntfc::getPrime)
			.collect(Collectors.toCollection(TreeSortedSet::newSet));

		prime.getPrimeBaseData().addPrimeBases(List.of(basePrimeColl), BaseTypes.THREETRIPLE);
	}
}
