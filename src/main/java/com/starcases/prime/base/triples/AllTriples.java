package com.starcases.prime.base.triples;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;
import lombok.NonNull;

/**
 *
 * Track specific item as part of a triple.
 *
 */
enum TripleIdx
{
	BOT,
	MID,
	TOP
}

/**
 *
 * Indicator to relationship between current sum of triple values vs the target Prime.
 *
 */
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

	SumConstraintState(Integer compToResult)
	{
		this.compToResult = compToResult;
	}

	static SumConstraintState getEnum(Integer compToResult)
	{
		return Arrays.stream(SumConstraintState.values())
				.filter(e -> Objects.nonNull(e.compToResult))
				.filter(e -> e.compToResult.equals(compToResult))
				.findAny()
				.orElse(NONMATCH);
	}

	public static SumConstraintState checkSumConstraints(
			@NonNull PrimeRefIntfc [] primeRefs,
			@NonNull PrimeRefIntfc targetPrime)
	{
		// sum the current Prime refs except for item indexed by array idxs
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
@Log
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
	public static ConditionConstraintState checkConditionConstraints(
			@NonNull PrimeRefIntfc [] primeRefs)
	{
		// TripleIdx - BOT,MID,TOP
		var cs = ConditionConstraintState.OK;
		var baseCount = 0;
		int [] bases = { 1, 2, 3 };
		BigInteger tmpPrime = null;
		for (var idx : TripleIdx.values())
		{
			var pr = primeRefs[idx.ordinal()];
			if (pr != null)
			{
				if (tmpPrime == null || tmpPrime.compareTo(pr.getPrime()) < 0)
				{
					tmpPrime = pr.getPrime();
				}
				else
				{
					cs = ConditionConstraintState.RANGE_ERROR;
				}

				final var index = baseCount;
				bases[index] = pr.getPrimeRefIdx();
				baseCount++;
			}
		}

		final var baseCountOk = baseCount == 3;
		if (!baseCountOk)
		{
			cs = ConditionConstraintState.MISSING_BASE;
		}
		else
		{
			if (Arrays.stream(bases).distinct().count() != 3)
			{
				cs = ConditionConstraintState.DUPE;
			}
		}

		return cs;
	}
}

/**
 *
 * Class implementing the logic for finding all viable triples.
 *
 */
@Log
public class AllTriples
{
	static final EnumSet<ConditionConstraintState> BAD_CONDITION_STATE =
			EnumSet.of(ConditionConstraintState.DUPE,
					ConditionConstraintState.RANGE_ERROR,
					ConditionConstraintState.MISSING_BASE);

	static final EnumSet<SumConstraintState> GOOD_SUM_STATE =
			EnumSet.of(SumConstraintState.MATCH,
					SumConstraintState.INCREMENT_SUM);

	@NonNull
	private  PrimeRefIntfc targetPrime;

	@NonNull
	private  PrimeSourceIntfc ps;

	public AllTriples(@NonNull PrimeSourceIntfc ps, @NonNull PrimeRefIntfc targetPrime)
	{
		this.targetPrime = targetPrime;
		this.ps = ps;
	}

	/**
	 * Move to next Prime ref (of specified component of triple) and manage tracking the resulting sum/condition state.
	 *
	 * @param idx
	 * @param triple
	 * @param sumConstraint
	 * @param conditionConstraint
	 */
	void nextPrimeRef(
						@NonNull TripleIdx idx,
						@NonNull PrimeRefIntfc [] triple,
						@NonNull SumConstraintState [] sumConstraint,
						@NonNull ConditionConstraintState [] conditionConstraint)
	{
		if (triple[idx.ordinal()] != null)
		{
			triple[idx.ordinal()].getNextPrimeRef().ifPresent( pr ->  triple[idx.ordinal()] = pr);
		}
		sumConstraint[0] = SumConstraintState.checkSumConstraints(triple, targetPrime);
		conditionConstraint[0] = ConditionConstraintState.checkConditionConstraints(triple);
	}

	/**
	 * Main entry point to this processsing - which processes a single Prime to produce a list of all viable triples
	 * which individually sum to the Prime.
	 *
	 * This is a "mostly brute force" method which is shown by pretty slow performance.
	 *
	 * Note:
	 * Performing the process for each prime ref in concurrent/parallel produces a speedup but since this is
	 * generating all combinations of valid sums, it is slow for more than small sets of primes.
	 */
	void process()
	{
		final PrimeRefIntfc [] triple =
			{
				ps.getPrimeRef(0).orElse(null),  // Prime 1
				ps.getPrimeRef(2).orElse(null), // Prime 3
				ps.getPrimeRef(4).orElse(null) // Prime 7
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
					this.nextPrimeRef(TripleIdx.BOT, triple, sumConstraint, conditionConstraint);
				}
				while(GOOD_SUM_STATE.contains(sumConstraint[0]) && !BAD_CONDITION_STATE.contains(conditionConstraint[0]));

				// reset inner loop
				triple[TripleIdx.BOT.ordinal()] =  ps.getPrimeRef(0).orElse(null);

				this.nextPrimeRef(TripleIdx.MID, triple, sumConstraint, conditionConstraint);
			}
			while(GOOD_SUM_STATE.contains(sumConstraint[0]) && !BAD_CONDITION_STATE.contains(conditionConstraint[0]));

			// reset inner loops
			triple[TripleIdx.MID.ordinal()] = ps.getPrimeRef(1).orElse(null);
			triple[TripleIdx.BOT.ordinal()] = ps.getPrimeRef(0).orElse(null);

			this.nextPrimeRef(TripleIdx.TOP, triple, sumConstraint, conditionConstraint);
		}
		while(GOOD_SUM_STATE.contains(sumConstraint[0]) && !BAD_CONDITION_STATE.contains(conditionConstraint[0]));
	}

	private void addPrimeBases(@NonNull PrimeRefIntfc prime, @NonNull PrimeRefIntfc [] vals)
	{
		var bs = Arrays.stream(vals)
			.filter(Objects::nonNull)
			.map(PrimeRefIntfc::getPrimeRefIdx)
			.toList();

		prime.getPrimeBaseData().addPrimeBase(bs, BaseTypes.THREETRIPLE);
	}
}
