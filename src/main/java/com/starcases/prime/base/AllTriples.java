package com.starcases.prime.base;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import lombok.NonNull;


// FIXME Known error ; low tuples are missing - i.e. tuples which include '1' as the first item in the tuple.
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
 * Indicator to relationship between current sum of triple values vs the target prime.
 *
 */
enum SumConstraintState
{
	/**
	 * Indicate sum is not a prime value and/or doesn't match the prime. Usable as a default in most cases.
	 */
	NONMATCH(null)
	{},

	/**
	 * indicate sum is lower than target prime
	 *
	 * (prime - sum).signNum
	 */
	INCREMENT_SUM(1)
	{},

	/**
	 * indicate sum is higher than target prime
	 *
	 * (prime - sum).signNum
	 */
	DECREMENT_SUM(-1)
	{},

	/**
	 * indicate sum matches target prime
	 *
	 * (prime - sum).signNum
	 */
	MATCH(0)
	{};

	final Integer compToResult;

	SumConstraintState(Integer compToResult)
	{
		this.compToResult = compToResult;
	}

	static SumConstraintState getEnum(@NonNull Integer compToResult)
	{
		return Arrays.stream(SumConstraintState.values()).filter(e -> Objects.nonNull(e.compToResult)).filter(e -> e.compToResult.equals(compToResult)).findAny().orElse(NONMATCH);
	}

	public static SumConstraintState checkSumConstraints(@NonNull Map<TripleIdx,
			Optional<PrimeRefIntfc>> primeRefs,
			@NonNull PrimeRefIntfc targetPrime,
			BigInteger [] vals,
			TripleIdx [] idxs)
	{
		// sum the current prime refs except for item indexed by array idxs
		var sum1 = Arrays.
						stream(TripleIdx.values())
						.filter(i ->  idxs == null || (idxs != null && Arrays.stream(idxs).allMatch(ii -> ii != i)) )
						.map(primeRefs::get)
						.filter(Objects::nonNull)
						.filter(Optional::isPresent)
						.map(Optional::get)
						.map(PrimeRefIntfc::getPrime)
						.reduce(BigInteger.ZERO, BigInteger::add);

		// sum the items in array vals [which should equate to overrides of the prime refs specified by array idxs.
		var sum2 = vals == null ? BigInteger.ZERO :
				Arrays.
				stream(vals).
				filter(Objects::nonNull).
				reduce(BigInteger.ZERO, BigInteger::add);

		// Create total sum from both sets which should be sourced from 3 items in one or the other of primeRefs or vals.
		var finalSum = sum1.add(sum2);

		// determine if sum is higher than prime, equal to prime, less than prime or just doesn't match for some reason.
		var sumComptoPrime = targetPrime.getPrime().compareTo(finalSum);

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
	 * 2 bases have the same prime value
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

	public static ConditionConstraintState checkConditionConstraints(
			@NonNull Map<TripleIdx, Optional<PrimeRefIntfc>> primeRefs,
			PrimeRefIntfc [] pOverrideRefs,
			TripleIdx [] idxs)
	{
		// TripleIdx - BOT,MID,TOP
		var cs = ConditionConstraintState.OK;
		var baseCount = 0;
		var bases = new HashSet<BigInteger>();
		BigInteger tmpPrime = null;
		for (var idx : TripleIdx.values())
		{
			var idxsNumProvided = idxs == null ? 0 : idxs.length;

			var cont = false;
			for (var i=0; i < idxsNumProvided; i++)
			{
				if (pOverrideRefs != null && idxs != null && idx == idxs[i] && pOverrideRefs[i] != null)
				{
					if (tmpPrime == null || tmpPrime.compareTo(pOverrideRefs[i].getPrime()) < 0)
					{
						tmpPrime = pOverrideRefs[i].getPrime();
					}
					else
					{
						cs = ConditionConstraintState.RANGE_ERROR;
					}

					baseCount++;
					bases.add(pOverrideRefs[i].getPrime());
					cont = true;
				}
			}

			if (cont)
				continue;

			var pr = primeRefs.get(idx);
			if (pr.isPresent())
			{
				if (tmpPrime == null || tmpPrime.compareTo(pr.get().getPrime()) < 0)
				{
					tmpPrime = pr.get().getPrime();
				}
				else
				{
					cs = ConditionConstraintState.RANGE_ERROR;
				}

				baseCount++;
				bases.add(primeRefs.get(idx).get().getPrime());
			}
		}

		final var baseCountOk = baseCount == 3;
		if (!baseCountOk)
		{
			cs = ConditionConstraintState.MISSING_BASE;
		}
		else
		{
			final var distinctBases = bases.size();
			final var distinctBaseCountOk = distinctBases == 3;
			if (!distinctBaseCountOk)
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
@NoArgsConstructor
class AllTriples
{
	@NonNull
	PrimeRefIntfc targetPrime;

	@NonNull
	PrimeSourceIntfc ps;

	/**
	 * Constructors - only need package visibility.
	 *
	 * @param ps
	 * @param targetPrime
	 */
	AllTriples(@NonNull PrimeSourceIntfc ps, @NonNull PrimeRefIntfc targetPrime)
	{
		this.ps = ps;
		this.targetPrime = targetPrime;
	}

	/**
	 * Sum the components of the triple
	 *
	 * @param triple
	 * @return
	 */
	BigInteger sum(@NonNull Map<TripleIdx, Optional<PrimeRefIntfc>> triple)
	{
		return triple
				.values()
				.stream()
				.filter(Objects::nonNull)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(PrimeRefIntfc::getPrime)
				.reduce(BigInteger.ZERO, BigInteger::add);
	}

	/**
	 * Move to next prime ref (of specified component of triple) and manage tracking the resulting sum/condition state.
	 *
	 * @param idx
	 * @param triple
	 * @param sumConstraint
	 * @param conditionConstraint
	 */
	void nextPrimeRef(
						@NonNull TripleIdx idx,
						@NonNull Map<TripleIdx, Optional<PrimeRefIntfc>> triple,
						@NonNull SumConstraintState [] sumConstraint,
						@NonNull ConditionConstraintState [] conditionConstraint)
	{
		 triple.computeIfPresent(idx, (t, op) -> op.flatMap(PrimeRefIntfc::getNextPrimeRef));
		 sumConstraint[0] = SumConstraintState.checkSumConstraints(triple, targetPrime, null, null);
		 conditionConstraint[0] = ConditionConstraintState.checkConditionConstraints(triple, null, null);
	}

	/**
	 * Utility method for extracting a string version of the bases. Probably should move to the log functionality.
	 *
	 * @param triple
	 * @return
	 */
	String bases(@NonNull Map<TripleIdx, Optional<PrimeRefIntfc>> triple)
	{
		return triple
				.values()
				.stream()
				.filter(Objects::nonNull)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(p -> p.getPrime().toString())
				.collect(Collectors.joining(","));
	}

	/**
	 * Main entry point to this processsing - which processes a single prime to produce a list of all viable triples
	 * which individually sum to the prime.
	 *
	 * This is a "mostly brute force" method which is shown by pretty slow performance.
	 *
	 * Note: Some enhancements are possible to reduce the size of the data range/domain which would speed things up a bit.
	 *       Note that simply processing each prime in a thread does cause a speedup but without implementing the prior
	 *       statement, the runtime for anything other than small datasets is very slow.
	 */
	void process()
	{
		final EnumSet<ConditionConstraintState> BAD_CONDITION_STATE = EnumSet.of(ConditionConstraintState.DUPE, ConditionConstraintState.RANGE_ERROR);
		final EnumSet<SumConstraintState> GOOD_SUM_STATE = EnumSet.of(SumConstraintState.MATCH, SumConstraintState.INCREMENT_SUM);

		Map<TripleIdx, Optional<PrimeRefIntfc>> triple = new TreeMap<>();
		triple.put(TripleIdx.TOP, ps.getPrimeRef(4) );
		triple.put(TripleIdx.MID, ps.getPrimeRef(2) );
		triple.put(TripleIdx.BOT,  ps.getPrimeRef(0) );

		SumConstraintState [] sumConstraint = {SumConstraintState.checkSumConstraints(triple, targetPrime, null, null)};
		ConditionConstraintState [] conditionConstraint = {ConditionConstraintState.MISSING_BASE};

		do // cur top
		{
			do // cur mid
			{
				do // cur bot
				{
					if (sumConstraint[0] == SumConstraintState.MATCH && conditionConstraint[0] != ConditionConstraintState.DUPE)
					{
						addPrimeBases(targetPrime, triple);
						break;
					}
					this.nextPrimeRef(TripleIdx.BOT, triple, sumConstraint, conditionConstraint);

				}
				while(GOOD_SUM_STATE.contains(sumConstraint[0]) && !BAD_CONDITION_STATE.contains(conditionConstraint[0]));

				// reset inner loop
				triple.put(TripleIdx.BOT,  ps.getPrimeRef(0) );

				this.nextPrimeRef(TripleIdx.MID, triple, sumConstraint, conditionConstraint);
			}
			while(GOOD_SUM_STATE.contains(sumConstraint[0]) && !BAD_CONDITION_STATE.contains(conditionConstraint[0]));

			// reset inner loops
			triple.put(TripleIdx.MID, ps.getPrimeRef(1) );
			triple.put(TripleIdx.BOT,  ps.getPrimeRef(0) );

			this.nextPrimeRef(TripleIdx.TOP, triple, sumConstraint, conditionConstraint);
		}
		while(GOOD_SUM_STATE.contains(sumConstraint[0]) && !BAD_CONDITION_STATE.contains(conditionConstraint[0]));
	}

	private void addPrimeBases(@NonNull PrimeRefIntfc prime, @NonNull Map<TripleIdx, Optional<PrimeRefIntfc>> vals)
	{
		var bs = new BitSet();
		vals.values().stream()
			.filter(Objects::nonNull)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(PrimeRefIntfc::getPrimeRefIdx).forEach(bs::set);

		prime.addPrimeBase(bs, BaseTypes.THREETRIPLE);
	}
}
