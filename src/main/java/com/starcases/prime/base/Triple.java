package com.starcases.prime.base;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Set;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

enum TripleIdx 
{ 
	BOT,
	MID,
	TOP
	;
	
	TripleIdx getNext()
	{
		return values()[Math.min(ordinal() + 1, TOP.ordinal())];
	}
	
	TripleIdx getPrev()
	{
		return values()[Math.max(ordinal() - 1, BOT.ordinal())];	
	}
}

enum SumConstraintState 
{ 
	/**
	 * Indicate sum is not a prime value and/or doesn't match the prime.
	 */
	NONMATCH(null),
	
	/**
	 * indicate sum is lower than target prime
	 */
	RAISE_SUM(1),
	
	/**
	 * indicate sum is higher than target prime
	 */
	LOWER_SUM(-1),
	
	/**
	 * indicate sum matches target prime
	 */
	MATCH(0)
	;
	
	public final Integer compToResult;
	
	SumConstraintState(Integer compToResult)
	{		
		this.compToResult = compToResult;
	}
	
	static Optional<SumConstraintState> getEnum(Integer compToResult)
	{
		return Arrays.stream(SumConstraintState.values()).filter(e -> e.compToResult.equals(compToResult)).findAny();
	}
	
	/**
	 * Attempt to find an prime with the provided diff and idx
	 *  Don't exceed diff.  The resulting prime and offset are
	 *  returned in the retPrimes and offsets params.
	 *  
	 * @param sumConstraint
	 * @param valueConstraint
	 * @param diff
	 * @param tripleIdx
	 * @param retPrimes
	 * @param offsets
	 */
	void offsetFn(
			ValueConstraintState valueConstraint, 
			BigInteger diff,  
			TripleIdx tripleIdx, 
			Map<TripleIdx,PrimeRefIntfc> retPrimes, 
			BigInteger [] offsets)
	{	
		System.out.println(String.format("offsetFn enter SUM constraint[%s]  value-contraint[%s] diff[%d] - retPrimes[%s]  offset[%d] ", 
				this.toString(), 
				valueConstraint.toString(),
				diff,
				retPrimes.values()
					.stream()
					.filter(Objects::nonNull)
					.map(pr -> pr.getPrime().toString())
					.collect(Collectors.joining(",")),
				offsets[tripleIdx.ordinal()]));
		
		Function<Optional<PrimeRefIntfc>, Optional<BigInteger>> dp = 
				p ->  p.flatMap(p1 ->  (this == SumConstraintState.LOWER_SUM ?  p1.getDistToNextPrime() : p1.getDistToPrevPrime()) );
		
		Optional<PrimeRefIntfc> p = Optional.of(primeRefs.get(tripleIdx));		
		BigInteger sum = BigInteger.ZERO;		
		do 
		{
			final var tmpSum = sum;
			Optional<BigInteger> bi = dp.apply(p).flatMap(dnp -> Optional.of(dnp.add(tmpSum)));
			if (bi.isEmpty())
				break;
						
			sum = bi.get();
			
			if (sum.compareTo(diff) <= 0)
			{
				p.ifPresent(p1 -> retPrimes.put(tripleIdx, p1));

				UnaryOperator<Optional<PrimeRefIntfc>> chgPrime = 
					pc ->  pc.flatMap(p1 -> (this == SumConstraintState.LOWER_SUM ?  p1.getNextPrimeRef() : p1.getPrevPrimeRef())  );
			
					p = chgPrime.apply(p);
			}
		}
		while (sum.compareTo(diff) < 0);
		
		offsets[tripleIdx.ordinal()] = sum;
		
		System.out.println(String.format("offsetFn exit SUM constraint[%s]  value-contraint[%s] diff[%d]  - retPrimes[%s]  offset[%d] ", 
				this.toString(), 
				valueConstraint.toString(),
				diff,
				retPrimes.values()
					.stream()
					.filter(Objects::nonNull)
					.map(pr -> pr.getPrime().toString())
					.collect(Collectors.joining(",")),
				offsets[tripleIdx.ordinal()]));
	}	
}

enum ValueConstraintState 
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
	OK 
}

@Log
@NoArgsConstructor
class Triple
{
	PrimeRefIntfc targetPrime;
	PrimeSourceIntfc ps;
	Function<PrimeRefIntfc, PrimeRefIntfc> initTop;
	Function<PrimeRefIntfc, PrimeRefIntfc> initBot;
	
	@Getter
	Map<TripleIdx, PrimeRefIntfc> primeRefs = new TreeMap<>();
		
	Triple(PrimeSourceIntfc ps, PrimeRefIntfc targetPrime, UnaryOperator<PrimeRefIntfc> initTop, UnaryOperator<PrimeRefIntfc> initBot)
	{
		this.ps = ps;
		this.targetPrime = targetPrime;
		this.initTop = initTop;		
		this.initBot = initBot;
	}
		 
	ValueConstraintState checkValueConstraints(BigInteger [] vals, TripleIdx [] idxs)
	{
		// TripleIdx - BOT,MID,TOP
		
		ValueConstraintState cs = ValueConstraintState.OK;
		int baseCount = 0;
		Set<BigInteger> bases = new HashSet<>();
		BigInteger tmpPrime = null;
		for (var idx : TripleIdx.values())
		{
			boolean cont = false;
			for (int i=0; i < idxs.length; i++)
			{
				if (idx == idxs[i])
				{			
					if (vals[i] != null)
					{
						if (tmpPrime == null || tmpPrime.compareTo(vals[i]) < 0)
						{
							tmpPrime = vals[i];
						}
						else 
						{
							cs = ValueConstraintState.RANGE_ERROR;
						}
						
						baseCount++;						
						bases.add(vals[i]);
						System.out.println(String.format("checkValueConstraints overrides - idx[%s] val[%d]", idx.toString(), vals[i]));
						cont = true;
					}
				}				
			}
		
			if (cont)	
				continue;
			
			PrimeRefIntfc pr = primeRefs.get(idx);
			if (pr != null)
			{	
				if (tmpPrime == null || tmpPrime.compareTo(pr.getPrime()) < 0)
				{
					tmpPrime = pr.getPrime();
				}
				else 
				{
					cs = ValueConstraintState.RANGE_ERROR;
				}
			
				System.out.println(String.format("checkValueConstraints existing  - idx[%s] val[%d]", idx.toString(), pr.getPrime()));
				baseCount++;
				bases.add(primeRefs.get(idx).getPrime());
			}
		}
		
		final var baseCountOk = baseCount == 3;
		if (!baseCountOk)
		{
			cs = ValueConstraintState.MISSING_BASE;
		}		
		else
		{	
			final var distinctBases = bases.size();
			final var distinctBaseCountOk = distinctBases == 3;
			if (!distinctBaseCountOk)
			{
				cs = ValueConstraintState.DUPE;
			}
		}
			
		return cs;
	}
	
	ValueConstraintState checkValueConstraints(BigInteger val, TripleIdx idx)
	{
		BigInteger [] vals = {val};
		TripleIdx [] idxs = {idx};
		return checkValueConstraints(vals, idxs);
	}

	Optional<TripleIdx> findDupeBase()
	{	
		TripleIdx dupeIdx = null;
		
		if (primeRefs.get(TripleIdx.BOT).equals(primeRefs.get(TripleIdx.MID)))
		{
			dupeIdx = TripleIdx.BOT;		
		}
		else if (primeRefs.get(TripleIdx.TOP).equals(primeRefs.get(TripleIdx.MID)))
		{
			dupeIdx = TripleIdx.TOP;		
		}
		
		return Optional.ofNullable(dupeIdx);	
	}
	
	BigInteger tgtP()
	{
		return this.targetPrime.getPrime();
	}

	SumConstraintState checkSumConstraints(BigInteger [] vals, TripleIdx [] idxs)
	{
		// sum the current prime refs except for item indexed by array idxs
		var sum1 = Arrays.
						stream(TripleIdx.values()).
						filter(i ->  Arrays.stream(idxs).allMatch(ii -> ii != i) ).
						map(i -> primeRefs.get(i)).
						filter(Objects::nonNull).
						map(PrimeRefIntfc::getPrime).
						reduce(BigInteger.ZERO, BigInteger::add);
		
		// sum the items in array vals [which should equate to overrides of the prime refs specified by array idxs.
		var sum2 = Arrays.
				stream(vals).
				filter(Objects::nonNull).
				reduce(BigInteger.ZERO, BigInteger::add);

		// Create total sum from both sets which should be sourced from 3 items in one or the other of primeRefs or vals.
		var finalSum = sum1.add(sum2);
		
		// determine if sum is higher than prime, equal to prime, less than prime or just doesn't match for some reason.
		var sumComptoPrime = finalSum.compareTo(targetPrime.getPrime());
			
		return
				switch(sumComptoPrime)
				{
				case -1 -> SumConstraintState.RAISE_SUM;
				case 1 -> SumConstraintState.LOWER_SUM;
				case 0 -> SumConstraintState.MATCH;
				default -> SumConstraintState.NONMATCH;
				};
	}

	SumConstraintState checkSumConstraints(BigInteger val, TripleIdx idx)
	{
		BigInteger [] vals = {val};
		TripleIdx [] idxs = {idx};
		return checkSumConstraints(vals, idxs);
 	}
	
	public BigInteger sum()
	{
		return primeRefs.values().stream().filter(Objects::nonNull).map(PrimeRefIntfc::getPrime).reduce(BigInteger.ZERO, BigInteger::add);
	}
	
	void log(String loc, SumConstraintState sumState, ValueConstraintState valState)
	{
		log.info(String.format("%s : prime:[%d]  sum:[%d]  vals:[%s]  sum-prime-diff:[%d] sum-constraint-state[%s] val-constraint-state[%s]",
				loc,
				targetPrime.getPrime(), 
				sum(), 
				primeRefs.values().stream().filter(Objects::nonNull).map(Object::toString)
				.collect(Collectors.joining(",", "[", "]")),
				tgtP().subtract(sum()),
				sumState != null ? sumState.toString() : "<constraint state unset>",
				valState != null ? valState.toString() : "<constraint state unset>"));
	}

	void lenter(String loc, SumConstraintState sumState, ValueConstraintState valState)
	{
		log("Handling item for " + loc, sumState, valState);
	}
	
	void lexit(String loc, SumConstraintState sumState, ValueConstraintState valState)
	{
		log("Completed item for " + loc, sumState, valState);
	}
	
	/**
	 * unconditionally set the prime ref to the provided data.
	 * 
	 * @param req
	 */
	ValueConstraintState setVal(PrimeRefIntfc prime, TripleIdx idx)
	{
		final String func = String.format("setVal(prime=%d, idx=%s)", prime.getPrime(), idx.toString());
		// Update prime ref for target idx		
		primeRefs.put(idx, prime);
		ValueConstraintState valueConstraintState = checkValueConstraints(prime.getPrime(), idx);
		lexit(func, null, valueConstraintState);
		return valueConstraintState;
	}

	/**
	 * conditionally set the prime ref to the provided data.
	 * 
	 * @param req
	 */
	ValueConstraintState setValidVal(Optional<PrimeRefIntfc> prime, TripleIdx idx)
	{
		TripleIdx [] localIdxs = { idx };
		BigInteger [] localPrimes = {null};
		
		final String func = String.format("setValidVal(prime=%s, idx=%s)  - enter", prime, idx.toString());
		lenter(func, null, null);
		
		ValueConstraintState[] valueConstraintState = {null};
		
		if (prime.isEmpty())
			valueConstraintState[0] = checkValueConstraints(localPrimes, localIdxs);
		
		prime.ifPresent( p ->
			{
				localPrimes[0] = p.getPrime();
				valueConstraintState[0] = checkValueConstraints(localPrimes, localIdxs);
				
				
				// Update prime ref for target idx if value constraint ok		
				if (valueConstraintState[0] == ValueConstraintState.OK)
					primeRefs.put(idx, p);				
			}
				);
		
		final String funcExit = String.format("setValidVal(prime=%s, idx=%s) - exit - final-state[%s]", prime, idx.toString(), valueConstraintState[0].name());
		lexit(funcExit, null, valueConstraintState[0]);
		return valueConstraintState[0];
	}	
	
	/**
	 * conditionally set the prime ref to the provided data.
	 * 
	 * @param req
	 */
	ValueConstraintState setValidVal(PrimeRefIntfc prime, TripleIdx idx)
	{
		ValueConstraintState valueConstraintState = checkValueConstraints(prime.getPrime(), idx);
		final String func = String.format("setValidVal(prime=%d, idx=%s) - final-state[%s]", prime.getPrime(), idx.toString(), valueConstraintState.name());
		// Update prime ref for target idx if value constraint ok		
		if (valueConstraintState == ValueConstraintState.OK)
			primeRefs.put(idx, prime);
		
		lexit(func, null, valueConstraintState);
		return valueConstraintState;
	}	
	/*
	boolean prev(TripleIdx idx, ValueConstraintState [] valueConstraintState)
	{
		final String func = String.format("prev(%s)", idx.toString());
		lenter(func, null, valueConstraintState[0]);
		boolean [] success = {false};
		primeRefs.get(idx)
				.getPrevPrimeRef()
				.ifPresent(pHigh -> 
					{ 
						int pHighIdx = pHigh.getPrimeRefIdx();
						int pLowerIdx = primeRefs.get(idx.getPrev()).getPrimeRefIdx();
						if (pHighIdx > pLowerIdx ) 
						{
							valueConstraintState[0] = setVal(pHigh, idx); 
							success[0] = true;  
						}
					});
		lexit(func, null, valueConstraintState[0]);
		return success[0];
	}

	
	boolean next(TripleIdx idx, ValueConstraintState [] valueConstraintState)
	{
		final String func = String.format("next(%s)", idx.toString());
		lenter(func, null, valueConstraintState[0]);
		boolean [] success = {false};
		primeRefs.get(idx)
			.getNextPrimeRef()
			.ifPresent(p -> 
				{
					if (p.getPrimeRefIdx() < primeRefs.get(idx.getNext()).getPrimeRefIdx() )
					{
						valueConstraintState[0] = setVal(p, idx);
						success[0] = true;
					}
				});
		lexit(func, null, valueConstraintState[0]);
		return success[0];
	} 
*/	
	void adjustTop()
	{
		var top = primeRefs.get(TripleIdx.TOP);
		var topPrev = top.getPrevPrimeRef().get();
		var topPrevPrev = topPrev.getPrevPrimeRef().get();
		var sum = top.getPrime().add(topPrev.getPrime()).add(topPrevPrev.getPrime());
		var primeSumDiff = targetPrime.getPrime().subtract(sum);
		var primeSumDiffCompto0 = primeSumDiff.compareTo(BigInteger.ZERO);
		
		if (primeSumDiffCompto0 > 0) // prime larger than sum; increase Top
		{
			System.out.println(String.format("Triple - %d top increase by add %d", top.getPrime(),  primeSumDiff));
			top = ps.getNearPrimeRef(top.getPrime().add(primeSumDiff)).get();									
		}
		else if (primeSumDiffCompto0 != 0) // check if too big
		{
			sum = top.getPrime().add(BigInteger.valueOf(3L));
			primeSumDiff = targetPrime.getPrime().subtract(sum);
			primeSumDiffCompto0 = primeSumDiff.compareTo(BigInteger.ZERO);
			if (primeSumDiffCompto0 < 0 ) // Prime smaller than sum; Reduce top
			{
				System.out.println(String.format("Triple - %d top reduce by adding %d", top.getPrime(), primeSumDiff));
				top = ps.getNearPrimeRef(top.getPrime().subtract(primeSumDiff)).get();
			}
		}
		setVal(top, TripleIdx.TOP);		
	}
	
	void adjustBot()
	{
		var top = primeRefs.get(TripleIdx.TOP);
		
		// adjust bot in case it could violate T > M > B based on potential sum combinations
		var bot = primeRefs.get(TripleIdx.BOT);
		var sum = top.getPrime().add(top.getPrevPrimeRef().get().getPrime()).add(bot.getPrime());
		var primeSumDiff = targetPrime.getPrime().subtract(sum);
		var primeSumDiffCompto0 = primeSumDiff.compareTo(BigInteger.ZERO);

		if (primeSumDiffCompto0 > 0) // prime larger than sum; Increase Bot
		{
			System.out.println(String.format("Triple - %d bot increase by add %d", bot.getPrime(),  primeSumDiff));
			bot = ps.getNearPrimeRef(bot.getPrime().add(primeSumDiff)).get();
		}
		else if (primeSumDiffCompto0 != 0) // check sum too small
		{				
			var topPrev = top.getPrevPrimeRef().get();
			
			sum = top.getPrime().add(topPrev.getPrime()).add(bot.getPrime());
			primeSumDiff = targetPrime.getPrime().subtract(sum);
			
			primeSumDiffCompto0 = primeSumDiff.compareTo(BigInteger.ZERO);
			if (primeSumDiffCompto0 < 0 ) // prime smaller than sum; reduce Bot
			{
				System.out.println(String.format("Triple - %d bot decrease by add %d", bot.getPrime(), primeSumDiff));
				bot = ps.getNearPrimeRef(bot.getPrime().add(primeSumDiff)).get();
			}		
		}
	
		setVal(bot, TripleIdx.BOT);
	}
	
	/**
	 * check offsets of top with mid offset negated; if enough then done otherwise
	 * also check offset of bottom to see if enough.
	 *  
	 * @param midPrime
	 * @param midPrimeDiff
	 * @return
	 */
	Optional<PrimeRefIntfc> fixNonPrimeMid(PrimeRefIntfc midPrime, BigInteger midPrimeDiff)
	{
		BigInteger [] offsets = {BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO};
		
		Map<TripleIdx,PrimeRefIntfc> retPrimeRefs = new TreeMap<>();
		Optional<PrimeRefIntfc> retPrimeRef = Optional.empty();
		
		BigInteger [] retPrimes = { null, null, null };
		TripleIdx [] idxs = { null, null, null };
		SumConstraintState sumCS = checkSumConstraints(retPrimes, idxs);
		
		SumConstraintState.getEnum(midPrimeDiff.signum()).flatMap( cs ->
				{
					// negative diff - lower top  - LOWER_SUM 
					cs.offsetFn( 
							ValueConstraintState.RANGE_ERROR, 
							midPrimeDiff,  
							TripleIdx.TOP, 
							retPrimeRefs, 
							offsets);
					
					retPrimes[TripleIdx.TOP.ordinal()] = retPrimeRefs.get(TripleIdx.TOP).getPrime();
					idxs[TripleIdx.TOP.ordinal()] = TripleIdx.TOP;
					
					sumCS = checkSumConstraints(retPrimes, idxs);
		
					// negative diff - lower bottom
					cs.offsetFn( 
							ValueConstraintState.RANGE_ERROR, 
							midPrimeDiff,  
							TripleIdx.BOT, 
							retPrimeRefs, 
							offsets);
					
					retPrimes[TripleIdx.BOT.ordinal()] = retPrimeRefs.get(TripleIdx.BOT).getPrime();
					idxs[TripleIdx.BOT.ordinal()] = TripleIdx.BOT;
					sumCS = checkSumConstraints(retPrimes, idxs);
				});

		// negative/pos diff - lower top / raise bottom
		
		if (midPrimeDiff != BigInteger.ZERO)
		{
			if (!midPrimeDiff.equals(offsets[TripleIdx.TOP.ordinal()]))
			{
				offsetFn(
						SumConstraintState.LOWER_SUM, 
						ValueConstraintState.RANGE_ERROR, 
						midPrimeDiff,  
						TripleIdx.TOP, 
						retPrimes, 
						offsets);
				
				offsetFn(
						SumConstraintState.RAISE_SUM, 
						ValueConstraintState.RANGE_ERROR, 
						midPrimeDiff,  
						TripleIdx.BOT, 
						retPrimes, 
						offsets);				
			}
			else
			{
				// negative/pos diff - raise top / lower bottom
				offsetFn(
						SumConstraintState.RAISE_SUM, 
						ValueConstraintState.RANGE_ERROR, 
						midPrimeDiff,  
						TripleIdx.TOP, 
						retPrimes, 
						offsets);
				
				offsetFn(
						SumConstraintState.LOWER_SUM, 
						ValueConstraintState.RANGE_ERROR, 
						midPrimeDiff,  
						TripleIdx.BOT, 
						retPrimes, 
						offsets);	
			}
		}		

		primeRefs
		.replace(TripleIdx.TOP, 
				ps.getPrimeRef(
						primeRefs.get(TripleIdx.TOP)
							.getPrime()
							.add(offsets[TripleIdx.TOP.ordinal()]))
				.get());
	
	retPrime = ps.getPrimeRef(midPrime.getPrime(), midPrimeDiff.negate());

		
		return retPrime;
	}
	
	public Optional<Map<TripleIdx, PrimeRefIntfc>> process()
	{	
		final String func = "process()";
		
		ValueConstraintState [] valueConstraintState = {null};
		SumConstraintState [] sumConstraintState = {null};
		
		lenter(func, sumConstraintState[0], valueConstraintState[0]);
		
		setVal(initTop.apply(targetPrime), TripleIdx.TOP);
		valueConstraintState[0] = setVal(initBot.apply(targetPrime), TripleIdx.BOT);
		
		adjustTop();
		adjustBot();
		
		var tgtPrimeSumDiff = tgtP().subtract(sum());		
		var tmpMidPrimeRef = ps.getNearPrimeRef(tgtPrimeSumDiff);
		
		tmpMidPrimeRef.ifPresent( localMidPrimeRef ->
					{
						Optional<PrimeRefIntfc> newMidPrimeRef = Optional.of(localMidPrimeRef);
						
						// spread top/bot to account for non-prime mid.
						if (!tgtPrimeSumDiff.equals(tmpMidPrimeRef.get().getPrime()))
							newMidPrimeRef = fixNonPrimeMid(localMidPrimeRef, tgtPrimeSumDiff);
						
						// Desire to continue on in a state where the mid value is not a dupe but may not generate correct sum.
						if ((valueConstraintState[0] = setValidVal(newMidPrimeRef, TripleIdx.MID)) != ValueConstraintState.OK)							
						{
							int offset = 1;
							while (valueConstraintState[0] != ValueConstraintState.OK)
							{
								Optional<PrimeRefIntfc> tp1 = ps.getPrimeRef(newMidPrimeRef.get().getPrimeRefIdx()+offset);			
								tp1.ifPresent( p1 ->   valueConstraintState[0] = setValidVal(p1, TripleIdx.MID) );

								if (valueConstraintState[0] != ValueConstraintState.OK)
								{
									Optional<PrimeRefIntfc> tp2 = ps.getPrimeRef(newMidPrimeRef.get().getPrimeRefIdx()-offset);			
									tp2.ifPresent( p ->   valueConstraintState[0] = setValidVal(p, TripleIdx.MID) );
								}						
								offset++;
							}						
						}						
					}
				);
				
		sumConstraintState[0] = checkSumConstraints(BigInteger.ZERO, null);
		
		// SumConstraintState  NONMATCH, LOW[sum < prime], HIGH[sum > prime], MATCH [sum==prime]
		// ValueConstraintState  MISSING_BASE, DUPE, OK 
		do
		{		
			switch(valueConstraintState[0])
				{					
				case OK:
					if (sumConstraintState[0] == SumConstraintState.MATCH)
						break;
					// otherwise fall-through if sum is not correct
				case DUPE:
					//this.findFirstOffsetMatch(sumConstraintState, valueConstraintState);
					break;
				
				case RANGE_ERROR:
					//this.findFirstOffsetMatch(sumConstraintState, valueConstraintState);
					break;
					
				case MISSING_BASE:
					log.severe("Process() - missing base; unexpected condition");
					break;					
				}
				
			log(func, sumConstraintState[0], valueConstraintState[0]);	
		}
		while (sumConstraintState[0] != SumConstraintState.MATCH && valueConstraintState[0] != ValueConstraintState.OK);
		
		
		
		return Optional.of(primeRefs);
	}
	/*
	void findFirstOffsetMatch(SumConstraintState [] sumConstraintState, ValueConstraintState [] valueConstraintState)
	{		
		final String func = "findFirstOffsetMatch()";
		
		if (primeRefs.values().stream().allMatch(Objects::nonNull))
		{
			int idxTopStart = primeRefs.get(TripleIdx.TOP).getPrimeRefIdx();
			int idxTopCur = idxTopStart;
			
			int idxBotStart = primeRefs.get(TripleIdx.BOT).getPrimeRefIdx();
			int idxBotCur = idxBotStart;		
			
			int idxMidStart = primeRefs.get(TripleIdx.MID).getPrimeRefIdx();
			int idxMidCur = idxMidStart;
			
			BigInteger diff = tgtP().subtract(sum());
			Optional<TripleIdx> dupeIdx = findDupeBase();
			
			// sign of diff 
			int sign = diff.signum();
			
			// Range of possible indexes +/-
			int tIdxRangePos  = ps.getMaxIdx() - primeRefs.get(TripleIdx.TOP).getPrimeRefIdx();
			int tmIdxRangePos = primeRefs.get(TripleIdx.TOP).getPrimeRefIdx() - primeRefs.get(TripleIdx.MID).getPrimeRefIdx();
			int mbIdxRangePos = primeRefs.get(TripleIdx.MID).getPrimeRefIdx() - primeRefs.get(TripleIdx.BOT).getPrimeRefIdx();
			int bIdxRangeNeg  = 0 - primeRefs.get(TripleIdx.BOT).getPrimeRefIdx();

			int idxMatchT[] = {-1};
			int idxMatchTM[] = {-1};
			int idxMatchMB[] = {-1};
			int idxMatchB[] = {-1};
			
			if (sign == 1)
			{
				IntStream.range(1, tIdxRangePos)
					.filter(i -> ps.getDistBetween(idxTopStart, i).equals(diff))
					.findAny()
					.ifPresent(ii -> idxMatchT[0] = ii);
				
				IntStream.range(1, tmIdxRangePos)
				.filter(i -> ps.getDistBetween(idxMidStart, i).equals(diff))
				.findAny()
				.ifPresent(ii -> idxMatchTM[0] = ii);
				
				IntStream.range(1, mbIdxRangePos)
				.filter(i -> ps.getDistBetween(idxBotStart, i).equals(diff))
				.findAny()
				.ifPresent(ii -> idxMatchMB[0] = ii);					
			}
			else if (sign == -1)
			{
				IntStream.range(1, tmIdxRangePos)
				.filter(i -> ps.getDistBetween(idxMidStart, -i).equals(diff))
				.findAny()
				.ifPresent(ii -> idxMatchTM[0] = ii);
				
				IntStream.range(1, mbIdxRangePos)
				.filter(i -> ps.getDistBetween(idxBotStart, -i).equals(diff))
				.findAny()
				.ifPresent(ii -> idxMatchMB[0] = ii);
				
				IntStream.range(1, bIdxRangeNeg)
				.filter(i -> ps.getDistBetween(idxBotStart, -i).equals(diff))
				.findAny()
				.ifPresent(ii -> idxMatchB[0] = ii);									
				
			}
				
			// SumConstraintState  NONMATCH, LOW[sum < prime], HIGH[sum > prime], MATCH [sum==prime]
			// ValueConstraintState  MISSING_BASE, DUPE, OK 	
			BigInteger [] offsets = { BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO }; 
			do
			{	
				lenter(func, sumConstraintState[0], valueConstraintState[0]);
				Map<TripleIdx,PrimeRefIntfc> retPrimes = new TreeMap<>(primeRefs);
				BigInteger [] vals = {null, null, null};
				TripleIdx [] idxs = {null, null, null};
				switch(sumConstraintState[0])
				{
				case HIGH:
						// SumConstraintState  NONMATCH, LOW[sum < prime], HIGH[sum > prime], MATCH [sum==prime]
						// ValueConstraintState  MISSING_BASE, DUPE, OK 						
						// dupeIdx is empty, TOP or BOT						
						if (sign == -1 && diff.compareTo(bRangeNeg) >= 0)
						{
							offsetFn(sumConstraintState[0], valueConstraintState[0], diff,  TripleIdx.BOT, retPrimes, offsets);
							
							vals[0] = retPrimes.get(TripleIdx.BOT).getPrime();
							idxs[0] = TripleIdx.BOT;

						}							
						else if (diff.compareTo(mbRange) >= 0)
						{
							offsetFn(sumConstraintState[0], valueConstraintState[0], diff,  TripleIdx.MID, retPrimes, offsets);							
							
							vals[0] = retPrimes.get(TripleIdx.MID).getPrime();
							idxs[0] = TripleIdx.MID;
							
						}
						else if (diff.compareTo(tmRange) >= 0)
						{
							offsetFn(sumConstraintState[0], valueConstraintState[0], diff,  TripleIdx.TOP, retPrimes, offsets);							
							
							vals[0] = retPrimes.get(TripleIdx.TOP).getPrime();
							idxs[0] = TripleIdx.TOP;
						}						
					break;
					
				case LOW:
					// SumConstraintState  NONMATCH, LOW[sum < prime], HIGH[sum > prime], MATCH [sum==prime]
					// ValueConstraintState  MISSING_BASE, DUPE, OK 					
									
				    if (diff.compareTo(mbRange) <= 0)
					{
						offsetFn(sumConstraintState[0], valueConstraintState[0], diff,  TripleIdx.MID, retPrimes, offsets);							
											
						vals[0] = retPrimes.get(TripleIdx.MID).getPrime();
						idxs[0] = TripleIdx.MID;						
					}
					else if (diff.compareTo(tmRange) <= 0)
					{
						offsetFn(sumConstraintState[0], valueConstraintState[0], diff,  TripleIdx.TOP, retPrimes, offsets);
						
						vals[0] = retPrimes.get(TripleIdx.TOP).getPrime();
						idxs[0] = TripleIdx.TOP;
					}
					else if (sign == 1 && diff.compareTo(tRangePos) <= 0)
					{
						offsetFn(sumConstraintState[0], valueConstraintState[0], diff,  TripleIdx.TOP, retPrimes, offsets);
						
						vals[0] = retPrimes.get(TripleIdx.TOP).getPrime();
						idxs[0] = TripleIdx.TOP;
					}
				    
					break;
					
				case MATCH:
					// SumConstraintState  NONMATCH, LOW[sum < prime], HIGH[sum > prime], MATCH [sum==prime]
					// ValueConstraintState  MISSING_BASE, DUPE, OK 					
					if (valueConstraintState[0] == ValueConstraintState.DUPE)
					{
						if (dupeIdx.isPresent())
						{
							TripleIdx didx = dupeIdx.get();
							if (didx == TripleIdx.TOP)
							{
								offsetFn(SumConstraintState.LOW, valueConstraintState[0], idxTopStart, idxTopCur, TripleIdx.TOP, primeRefs, offsets);
								offsetFn(SumConstraintState.HIGH, valueConstraintState[0], idxTopStart, idxTopCur, TripleIdx.MID, primeRefs, offsets);
							}
							else
							{
								offsetFn(SumConstraintState.LOW, valueConstraintState[0], idxTopStart, idxTopCur, TripleIdx.MID, primeRefs, offsets);
								offsetFn(SumConstraintState.HIGH, valueConstraintState[0], idxTopStart, idxTopCur, TripleIdx.BOT, primeRefs, offsets);			
							}
						}
					}
					break;
					
				case NONMATCH:
					// SumConstraintState  NONMATCH, LOW[sum < prime], HIGH[sum > prime], MATCH [sum==prime]
					// ValueConstraintState  MISSING_BASE, DUPE, OK 					
					log.severe("findFirstOffsetMatch() sum constraint is NONMATCH - unexpected condition.");
					break;
				}

				sumConstraintState[0] = checkSumConstraints(vals, idxs);
				
				BigInteger sum = Arrays.stream(offsets).reduce(BigInteger.ZERO, BigInteger::add);
		
				
				System.out.println(String.format("findFirstOffsetMatch - loop constraint check - sum[%d] sum-constraint-state[%s] val-constraint-state[%s]", 
						sum, 
						Arrays.toString(sumConstraintState),
						Arrays.toString(valueConstraintState)));
			}
			while (sumConstraintState[0] != SumConstraintState.MATCH);	
			
		}
		
		lexit(func, sumConstraintState[0], valueConstraintState[0]);
	}
*/
			
	void offsetFn(SumConstraintState sumConstraint, ValueConstraintState valueConstraint, int idxStart, int idxCur, TripleIdx tripleIdx, Map<TripleIdx,PrimeRefIntfc> retPrimes, BigInteger [] offsets)
	{
		ps.getPrimeRef(idxCur).ifPresent(rp -> retPrimes.put(tripleIdx, rp));
		
		Function<PrimeRefIntfc, Optional<BigInteger>> dp = p -> sumConstraint == SumConstraintState.RAISE_SUM ?  p.getDistToNextPrime() : p.getDistToPrevPrime();
		
		offsets[tripleIdx.ordinal()] = IntStream			
					.rangeClosed(sumConstraint == SumConstraintState.RAISE_SUM ? idxStart : idxCur, sumConstraint == SumConstraintState.RAISE_SUM ? idxCur : idxStart)
					.boxed()
					.map(ps::getPrimeRef)
					.filter(Optional::isPresent)
					.map(p1 -> dp.apply(p1.get()))
					.flatMap(Optional::stream)
					.reduce(offsets[tripleIdx.ordinal()], BigInteger::add);
		
		System.out.println(String.format("findFirstOffsetMatch SUM constraint[%s]  value-contraint[%s] - offset[%d] ", 
				sumConstraint.toString(), 
				valueConstraint.toString(), 
				offsets[tripleIdx.ordinal()]));
	}
	
	/**
	 * Attempt to find an prime with the provided diff and idx
	 *  Don't exceed diff.  The resulting prime and offset are
	 *  returned in the retPrimes and offsets params.
	 *  
	 * @param sumConstraint
	 * @param valueConstraint
	 * @param diff
	 * @param tripleIdx
	 * @param retPrimes
	 * @param offsets
	 */
	/*
	void offsetFn(
			SumConstraintState sumConstraint, 
			ValueConstraintState valueConstraint, 
			BigInteger diff,  
			TripleIdx tripleIdx, 
			Map<TripleIdx,PrimeRefIntfc> retPrimes, 
			BigInteger [] offsets)
	{	
		System.out.println(String.format("offsetFn enter SUM constraint[%s]  value-contraint[%s] diff[%d] - retPrimes[%s]  offset[%d] ", 
				sumConstraint.toString(), 
				valueConstraint.toString(),
				diff,
				retPrimes.values()
					.stream()
					.filter(Objects::nonNull)
					.map(pr -> pr.getPrime().toString())
					.collect(Collectors.joining(",")),
				offsets[tripleIdx.ordinal()]));
		
		Function<Optional<PrimeRefIntfc>, Optional<BigInteger>> dp = 
				p ->  p.flatMap(p1 ->  (sumConstraint == SumConstraintState.LOWER_SUM ?  p1.getDistToNextPrime() : p1.getDistToPrevPrime()) );
		
		Optional<PrimeRefIntfc> p = Optional.of(primeRefs.get(tripleIdx));		
		BigInteger sum = BigInteger.ZERO;		
		do 
		{
			final var tmpSum = sum;
			Optional<BigInteger> bi = dp.apply(p).flatMap(dnp -> Optional.of(dnp.add(tmpSum)));
			if (bi.isEmpty())
				break;
						
			sum = bi.get();
			
			if (sum.compareTo(diff) <= 0)
			{
				p.ifPresent(p1 -> retPrimes.put(tripleIdx, p1));

				UnaryOperator<Optional<PrimeRefIntfc>> chgPrime = 
					pc ->  pc.flatMap(p1 -> (sumConstraint == SumConstraintState.LOWER_SUM ?  p1.getNextPrimeRef() : p1.getPrevPrimeRef())  );
			
					p = chgPrime.apply(p);
			}
		}
		while (sum.compareTo(diff) < 0);
		
		offsets[tripleIdx.ordinal()] = sum;
		
		System.out.println(String.format("offsetFn exit SUM constraint[%s]  value-contraint[%s] diff[%d]  - retPrimes[%s]  offset[%d] ", 
				sumConstraint.toString(), 
				valueConstraint.toString(),
				diff,
				retPrimes.values()
					.stream()
					.filter(Objects::nonNull)
					.map(pr -> pr.getPrime().toString())
					.collect(Collectors.joining(",")),
				offsets[tripleIdx.ordinal()]));
	}	
	*/
}
	