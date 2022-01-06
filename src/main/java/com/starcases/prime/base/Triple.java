package com.starcases.prime.base;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Min;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import lombok.NonNull;

enum TripleIdx 
{ 
	BOT,
	MID,
	TOP;
	
	TripleIdx getNext()
	{
		return values()[Math.min(ordinal() + 1, TOP.ordinal())];
	}
	
	TripleIdx getPrev()
	{
		return values()[Math.max(ordinal() - 1, BOT.ordinal())];	
	}
}

@Log
enum SumConstraintState 
{ 
	/**
	 * Indicate sum is not a prime value and/or doesn't match the prime.
	 */
	NONMATCH(null)
	{},
	
	/**
	 * indicate sum is lower than target prime
	 * 
	 * (prime - sum).signNum
	 */
	RAISE_SUM(1)  
	{},
	
	/**
	 * indicate sum is higher than target prime
	 * 
	 * (prime - sum).signNum
	 */
	LOWER_SUM(-1)
	{},
	
	/**
	 * indicate sum matches target prime
	 * 
	 * (prime - sum).signNum
	 */
	MATCH(0)
	{};
	
	public final Integer compToResult;
	
	@NonNull
	final UnaryOperator<Optional<PrimeRefIntfc>> uopChgPrime = 
			pc ->  pc.flatMap(p1 -> (this == SumConstraintState.LOWER_SUM ? p1.getPrevPrimeRef() : p1.getNextPrimeRef() ) );

	@NonNull
	final Function<Optional<PrimeRefIntfc>, Optional<BigInteger>> fnDistToPrime = 
			p ->  p.flatMap(p1 ->  (this == SumConstraintState.LOWER_SUM ? p1.getDistToPrevPrime() : p1.getDistToNextPrime() ) );
					
	SumConstraintState(Integer compToResult)
	{		
		this.compToResult = compToResult;
	}
	
	public void symetricChangeSumRange(@NonNull BigInteger primeSumDiff, @NonNull Map<TripleIdx,PrimeRefIntfc> retPrimeRefs, @NonNull BigInteger [] retOffset)
	{
		final var func = String.format("moveSumRange(primeSumDiff[%d])", primeSumDiff);
		log.info("enter " + func);
		
		// negative diff -> lower top = LOWER_SUM;  positive diff -> increase top = RAISE_SUM 
		offsetFn( 
				primeSumDiff,  
				TripleIdx.TOP, 
				retPrimeRefs, 
				retOffset);
		
		// negative diff -> lower bottom = LOWER_SUM; positive diff -> increase bottom = RAISE_SUM
		offsetFn( 
				primeSumDiff,  
				TripleIdx.BOT, 
				retPrimeRefs, 
				retOffset);
		
		log.info("exit " + func);
	}
	
	public void incOrDecSumRange(@NonNull BigInteger primeSumDiff, @NonNull Map<TripleIdx,PrimeRefIntfc> retPrimeRefs, @NonNull BigInteger [] retOffset, boolean swapOrder)
	{
		final var func = String.format("incOrDecSumRange(diff[%d] swap[%b])", primeSumDiff, swapOrder);
		log.info("enter " + func);
		
		// negative diff or pos diff ->  
		// 			lower top & raise bottom = RAISE or LOWSER sum depending on size of each change  
		// 			raise top & lower bottom = RAISE or LOWSER sum depending on size of each change
		TripleIdx [] idxs = { TripleIdx.TOP, TripleIdx.BOT};
		if (swapOrder)
		{
			idxs[0] = TripleIdx.BOT;
			idxs[1] = TripleIdx.TOP;
		}
		offsetFn(
				primeSumDiff,  
				idxs[0], 
				retPrimeRefs, 
				retOffset);
		
		offsetFn(
				primeSumDiff.add(retOffset[0]),  
				idxs[1], 
				retPrimeRefs, 
				retOffset);
		
		log.info("exit " + func);
	}
	
	static SumConstraintState getEnum(@NonNull Integer compToResult)
	{
		return Arrays.stream(SumConstraintState.values()).filter(e -> Objects.nonNull(e.compToResult)).filter(e -> e.compToResult.equals(compToResult)).findAny().orElse(NONMATCH);
	}
	
	/**
	 * Attempt to find an prime with the provided diff for the provided idx
	 *  The resulting prime and offset are
	 *  returned in the retPrimes and offsets params.
	 *  
	 * @param sumConstraint
	 * @param valueConstraint
	 * @param diff
	 * @param tripleIdx
	 * @param retPrimes
	 * @param retOffset
	 */
	void offsetFn(
			@NonNull BigInteger diff,  
			@NonNull TripleIdx tripleIdx, 
			@NonNull Map<TripleIdx,PrimeRefIntfc> retPrimeRefs, 
			@NonNull BigInteger [] retOffset)
	{	
		assert(retOffset.length == 1);
		
		log.info(String.format("offsetFn enter SUM constraint[%s] idx[%s] diff[%d] - retPrimes[%s]  offset[%d] ", 
				this.toString(),
				tripleIdx.toString(),
				diff,
				retPrimeRefs.values()
					.stream()
					.filter(Objects::nonNull)
					.map(pr -> pr.getPrime().toString())
					.collect(Collectors.joining(",")),
				retOffset[0]));
				
		var p = Optional.ofNullable(retPrimeRefs.get(tripleIdx));	
		p.ifPresent( 
				pRef -> pRef
						.getPrimeRefWithinOffset(diff)
						.ifPresent(tgtRef -> { 
												// fail if constraints violated
												ValueConstraintState vcs = ValueConstraintState.checkValueConstraint(retPrimeRefs, tgtRef, tripleIdx);
												if (vcs == ValueConstraintState.OK)
												{
													retPrimeRefs.put(tripleIdx, tgtRef); 
													retOffset[0] = pRef.getPrime().subtract(tgtRef.getPrime());
												}
												else
												{
													log.severe(String.format("offsetFn - vcs[%s]", vcs));
												}
												})
				);
		
		log.info(String.format("offsetFn exit SUM constraint[%s] idx[%s] diff[%d]  - retPrimes[%s]  offset[%d] ", 
				this.toString(), 
				tripleIdx.toString(),
				diff,
				retPrimeRefs.values()
					.stream()
					.filter(Objects::nonNull)
					.map(pr -> pr.getPrime().toString())
					.collect(Collectors.joining(",")),
				retOffset[0]));
	}	
	
	public static SumConstraintState checkSumConstraints(@NonNull Map<TripleIdx, PrimeRefIntfc> primeRefs, @NonNull PrimeRefIntfc targetPrime, BigInteger [] vals, TripleIdx [] idxs)
	{
		// sum the current prime refs except for item indexed by array idxs
		var sum1 = Arrays.
						stream(TripleIdx.values()).
						filter(i ->  idxs == null || Arrays.stream(idxs).allMatch(ii -> ii != i) ).
						map(primeRefs::get).
						filter(Objects::nonNull).
						map(PrimeRefIntfc::getPrime).
						reduce(BigInteger.ZERO, BigInteger::add);
		
		// sum the items in array vals [which should equate to overrides of the prime refs specified by array idxs.
		var sum2 = vals == null ? BigInteger.ZERO : 
				Arrays.
				stream(vals).
				filter(Objects::nonNull).
				reduce(BigInteger.ZERO, BigInteger::add);

		// Create total sum from both sets which should be sourced from 3 items in one or the other of primeRefs or vals.
		var finalSum = sum1.add(sum2);
		
		// determine if sum is higher than prime, equal to prime, less than prime or just doesn't match for some reason.
		var sumComptoPrime = finalSum.compareTo(targetPrime.getPrime());
			
		return SumConstraintState.getEnum(sumComptoPrime);
	}

	public static SumConstraintState checkSumConstraint(@NonNull Map<TripleIdx, PrimeRefIntfc> primeRefs,@NonNull PrimeRefIntfc targetPrime, @NonNull BigInteger val, TripleIdx idx)
	{
		BigInteger [] vals = {val};
		TripleIdx [] idxs = {idx};
		return checkSumConstraints(primeRefs, targetPrime, vals, idxs);
 	}
}

@Log
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
	OK;
	
	public static ValueConstraintState checkValueConstraints(@NonNull Map<TripleIdx, PrimeRefIntfc> primeRefs, PrimeRefIntfc [] pOverrideRefs, TripleIdx [] idxs)
	{
		// TripleIdx - BOT,MID,TOP
		var cs = ValueConstraintState.OK;
		var baseCount = 0;
		var bases = new HashSet<BigInteger>();
		BigInteger tmpPrime = null;
		for (var idx : TripleIdx.values())
		{
			var idxsNumProvided = idxs == null ? 0 : idxs.length;
			
			var cont = false;
			for (var i=0; i < idxsNumProvided; i++)
			{
				if (idx == idxs[i] && pOverrideRefs != null && pOverrideRefs[i] != null)
				{			
					if (tmpPrime == null || tmpPrime.compareTo(pOverrideRefs[i].getPrime()) < 0)
					{
						tmpPrime = pOverrideRefs[i].getPrime();
					}
					else 
					{
						cs = ValueConstraintState.RANGE_ERROR;
					}
					
					baseCount++;						
					bases.add(pOverrideRefs[i].getPrime());
					log.finest(String.format("checkValueConstraints overrides - idx[%s] val[%d]", idx.toString(), pOverrideRefs[i].getPrime()));
					cont = true;
				}				
			}
		
			if (cont)	
				continue;
			
			var pr = primeRefs.get(idx);
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
			
				log.finest(String.format("checkValueConstraints existing  - idx[%s] val[%d]", idx.toString(), pr.getPrime()));
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
	
	public static ValueConstraintState checkValueConstraint(@NonNull Map<TripleIdx, PrimeRefIntfc> primeRefs, @NonNull PrimeRefIntfc pOverrideRef, TripleIdx idx)
	{
		PrimeRefIntfc [] pRefs = {pOverrideRef};
		TripleIdx [] idxs = {idx};
		return checkValueConstraints(primeRefs, pRefs, idxs);
	}	
}

@Log
@NoArgsConstructor
class Triple
{
	@NonNull
	PrimeRefIntfc targetPrime;
	
	@NonNull
	PrimeSourceIntfc ps;
	
	@NonNull
	Function<PrimeRefIntfc, PrimeRefIntfc> initTop;
	
	@NonNull
	Function<PrimeRefIntfc, PrimeRefIntfc> initBot;
	
	@Getter
	final Map<TripleIdx, PrimeRefIntfc> primeRefs = new TreeMap<>();
		
	Triple(@NonNull PrimeSourceIntfc ps, @NonNull PrimeRefIntfc targetPrime, @NonNull UnaryOperator<PrimeRefIntfc> initTop, @NonNull UnaryOperator<PrimeRefIntfc> initBot)
	{
		this.ps = ps;
		this.targetPrime = targetPrime;
		this.initTop = initTop;		
		this.initBot = initBot;
	}
		 
	BigInteger tgtP()
	{
		return this.targetPrime.getPrime();
	}

	public BigInteger sum()
	{
		return primeRefs.values().stream().filter(Objects::nonNull).map(PrimeRefIntfc::getPrime).reduce(BigInteger.ZERO, BigInteger::add);
	}
	
	void log(@NonNull @NotEmpty String loc, SumConstraintState sumState, ValueConstraintState valState)
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

	void lenter(@NonNull @NotEmpty String loc, SumConstraintState sumState, ValueConstraintState valState)
	{
		log("Handling item for " + loc, sumState, valState);
	}
	
	void lexit(@NonNull @NotEmpty String loc, SumConstraintState sumState, ValueConstraintState valState)
	{
		log("Completed item for " + loc, sumState, valState);
	}
	
	/**
	 * unconditionally set the prime ref to the provided data.
	 * 
	 * @param req
	 */
	ValueConstraintState setVal(@NonNull PrimeRefIntfc prime, @NonNull TripleIdx idx)
	{
		final var func = String.format("setVal(prime=%d, idx=%s)", prime.getPrime(), idx.toString());
		// Update prime ref for target idx		
		primeRefs.put(idx, prime);
		var valueConstraintState = ValueConstraintState.checkValueConstraint(primeRefs, prime, idx);
		lexit(func, null, valueConstraintState);
		return valueConstraintState;
	}

	/**
	 * conditionally set the prime ref to the provided data.
	 * 
	 * @param req
	 */
	ValueConstraintState setValidVal(@NonNull Optional<PrimeRefIntfc> prime, @NonNull TripleIdx idx)
	{
		TripleIdx [] localIdxs = { idx };
		PrimeRefIntfc [] localPrimes = {null};
		
		final var func = String.format("setValidVal(prime=%s, idx=%s)  - enter", prime, idx.toString());
		lenter(func, null, null);
		
		ValueConstraintState[] valueConstraintState = {null};
		
		if (prime.isEmpty())
			valueConstraintState[0] = ValueConstraintState.checkValueConstraints(primeRefs, localPrimes, localIdxs);
		
		prime.ifPresent( p ->
					{
						localPrimes[0] = p;
						valueConstraintState[0] = ValueConstraintState.checkValueConstraints(primeRefs, localPrimes, localIdxs);
						
						// Update prime ref for target idx if value constraint ok		
						if (valueConstraintState[0] == ValueConstraintState.OK)
							primeRefs.put(idx, p);				
					}
				);
		
		final var funcExit = String.format("setValidVal(prime=%s, idx=%s) - exit - final-state[%s]", prime, idx.toString(), valueConstraintState[0].name());
		lexit(funcExit, null, valueConstraintState[0]);
		return valueConstraintState[0];
	}	
	
	/**
	 * conditionally set the prime ref to the provided data.
	 * 
	 * @param req
	 */
	ValueConstraintState setValidVal(@NonNull PrimeRefIntfc prime, @NonNull TripleIdx idx)
	{
		var valueConstraintState = ValueConstraintState.checkValueConstraint(primeRefs, prime, idx);
		final var func = String.format("setValidVal(prime=%d, idx=%s) - final-state[%s]", prime.getPrime(), idx.toString(), valueConstraintState.name());
		// Update prime ref for target idx if value constraint ok		
		if (valueConstraintState == ValueConstraintState.OK)
			primeRefs.put(idx, prime);
		
		lexit(func, null, valueConstraintState);
		return valueConstraintState;
	}	
	
	void adjustTop()
	{
		var top = primeRefs.get(TripleIdx.TOP);
		var topPrev = top.getPrevPrimeRef().orElseThrow();
		var topPrevPrev = topPrev.getPrevPrimeRef().orElseThrow();
		var sum = top.getPrime().add(topPrev.getPrime()).add(topPrevPrev.getPrime());
		var primeSumDiff = targetPrime.getPrime().subtract(sum);
		var primeSumDiffCompto0 = primeSumDiff.compareTo(BigInteger.ZERO);
		
		if (primeSumDiffCompto0 > 0) // prime larger than sum; increase Top
		{
			log.finest(String.format("Triple - %d top increase by add %d", top.getPrime(),  primeSumDiff));
			top = ps.getNearPrimeRef(top.getPrime().add(primeSumDiff)).orElseThrow();									
		}
		else if (primeSumDiffCompto0 != 0) // check if too big
		{
			sum = top.getPrime().add(BigInteger.valueOf(3L));
			primeSumDiff = targetPrime.getPrime().subtract(sum);
			primeSumDiffCompto0 = primeSumDiff.compareTo(BigInteger.ZERO);
			if (primeSumDiffCompto0 < 0 ) // Prime smaller than sum; Reduce top
			{
				log.finest(String.format("Triple - %d top reduce by adding %d", top.getPrime(), primeSumDiff));
				top = ps.getNearPrimeRef(top.getPrime().subtract(primeSumDiff)).orElseThrow();
			}
		}
		setVal(top, TripleIdx.TOP);		
	}
	
	void adjustBot()
	{
		var top = primeRefs.get(TripleIdx.TOP);
		
		// adjust bot in case it could violate T > M > B based on potential sum combinations
		var bot = primeRefs.get(TripleIdx.BOT);
		var sum = top.getPrime().add(top.getPrevPrimeRef().orElseThrow().getPrime()).add(bot.getPrime());
		var primeSumDiff = targetPrime.getPrime().subtract(sum);
		var primeSumDiffCompto0 = primeSumDiff.compareTo(BigInteger.ZERO);

		if (primeSumDiffCompto0 > 0) // prime larger than sum; Increase Bot
		{
			log.finest(String.format("Triple - %d bot increase by add %d", bot.getPrime(),  primeSumDiff));
			bot = ps.getNearPrimeRef(bot.getPrime().add(primeSumDiff)).orElseThrow();
		}
		else if (primeSumDiffCompto0 != 0) // check sum too small
		{				
			var topPrev = top.getPrevPrimeRef().orElseThrow();
			
			sum = top.getPrime().add(topPrev.getPrime()).add(bot.getPrime());
			primeSumDiff = targetPrime.getPrime().subtract(sum);
			
			primeSumDiffCompto0 = primeSumDiff.compareTo(BigInteger.ZERO);
			if (primeSumDiffCompto0 < 0 ) // prime smaller than sum; reduce Bot
			{
				log.finest(String.format("Triple - %d bot decrease by add %d", bot.getPrime(), primeSumDiff));
				bot = ps.getNearPrimeRef(bot.getPrime().add(primeSumDiff)).orElseThrow();
			}		
		}
	
		setVal(bot, TripleIdx.BOT);
	}
	
	/**
	 * perform operations to provide a prime which meets all criteria for the mid item.
	 *  
	 * @param midPrime
	 * @param midPrimeDiff
	 * @return
	 */
	void handleMid(@NonNull PrimeRefIntfc targetPrime, @NonNull @Min(0) BigInteger curSum)
	{			
		var tgtPrimeSumDiff = targetPrime.getPrime().subtract(curSum);		
		var tmpMidPrimeRef = ps.getNearPrimeRef(tgtPrimeSumDiff);
		
		final var func = String.format("handleMid(tgtPrime[%d], curSum[%d]) tgtPrimeSumDiff[%d] tgtMidPrimeRef[%s]", 
				targetPrime.getPrime(), curSum, tgtPrimeSumDiff, tmpMidPrimeRef.get());
		lenter(func, null, null);
		
		BigInteger [] tmpPrimeSumDiff = { BigInteger.ZERO };
		ValueConstraintState [] valueConstraintState = { ValueConstraintState.MISSING_BASE};
		SumConstraintState [] sumConstraintState = { SumConstraintState.NONMATCH };
		
		tmpMidPrimeRef.ifPresent(pMidRef -> 
										{
											valueConstraintState[0] = setValidVal(pMidRef, TripleIdx.MID);
											tmpPrimeSumDiff[0] = targetPrime.getPrime().subtract(sum());
										}
								);

		if (valueConstraintState[0] == ValueConstraintState.OK && tmpPrimeSumDiff[0] == BigInteger.ZERO)
			return; // Done / nothing else to do

		var retPrimeRefs = new TreeMap<TripleIdx,PrimeRefIntfc>(primeRefs);
		BigInteger [] retOffset = {BigInteger.ZERO};
		valueConstraintState[0] = ValueConstraintState.checkValueConstraints(primeRefs, null, null);
		sumConstraintState[0] = SumConstraintState.checkSumConstraints(primeRefs, targetPrime, null, null);
		
		SumConstraintState
		.getEnum(tmpPrimeSumDiff[0].signum())
		.symetricChangeSumRange( 
				tmpPrimeSumDiff[0].add(retOffset[0]),  							 
				retPrimeRefs, 
				retOffset);
		
		valueConstraintState[0] = ValueConstraintState.checkValueConstraints(primeRefs, null, null);
		sumConstraintState[0] = SumConstraintState.checkSumConstraints(primeRefs, targetPrime, null, null);
		
		retPrimeRefs.forEach( (k,v) -> setValidVal(v, k));

		valueConstraintState[0] = ValueConstraintState.checkValueConstraints(primeRefs, null, null);
		sumConstraintState[0] = SumConstraintState.checkSumConstraints(primeRefs, targetPrime, null, null);
		
		SumConstraintState
		.getEnum(tmpPrimeSumDiff[0].signum())
		.incOrDecSumRange( 
				tmpPrimeSumDiff[0].add(retOffset[0]),  							 
				retPrimeRefs, 
				retOffset,
				false);

		valueConstraintState[0] = ValueConstraintState.checkValueConstraints(primeRefs, null, null);
		sumConstraintState[0] = SumConstraintState.checkSumConstraints(primeRefs, targetPrime, null, null);

		retPrimeRefs.forEach( (k,v) -> setValidVal(v, k));

		valueConstraintState[0] = ValueConstraintState.checkValueConstraints(primeRefs, null, null);
		sumConstraintState[0] = SumConstraintState.checkSumConstraints(primeRefs, targetPrime, null, null);
		if (sumConstraintState[0] != SumConstraintState.MATCH)
		{
			SumConstraintState
			.getEnum(tmpPrimeSumDiff[0].signum())
			.incOrDecSumRange( 
					tmpPrimeSumDiff[0].add(retOffset[0]),  							 
					retPrimeRefs, 
					retOffset,
					true);			
		
			retPrimeRefs.forEach( (k,v) -> setValidVal(v, k));
		}
		
		lexit(func, null, null);
	}
	
	public Optional<Map<TripleIdx, PrimeRefIntfc>> process()
	{	
		final var func = "process()";
		
		ValueConstraintState [] valueConstraintState = {null};
		SumConstraintState [] sumConstraintState = {null};
		
		lenter(func, sumConstraintState[0], valueConstraintState[0]);
		
		setVal(initTop.apply(targetPrime), TripleIdx.TOP);
		valueConstraintState[0] = setVal(initBot.apply(targetPrime), TripleIdx.BOT);
		
		adjustTop();
		adjustBot();	
		handleMid(targetPrime, sum());
		
		sumConstraintState[0] = SumConstraintState.checkSumConstraints(primeRefs, targetPrime, null, null);
		valueConstraintState[0] = ValueConstraintState.checkValueConstraints(primeRefs, null, null);
		
		
		switch(valueConstraintState[0])
			{					
			case OK:
				if (sumConstraintState[0] != SumConstraintState.MATCH)
					log.severe("Process() - OK but non-match; unexpected condition");
				break;
				
			case DUPE:
				log.severe("Process() - dupe; unexpected condition");
				break;
			
			case RANGE_ERROR:
				log.severe("Process() - range error; unexpected condition");
				break;
				
			case MISSING_BASE:
				log.severe("Process() - missing base; unexpected condition");
				break;					
			}
				
		log(func, sumConstraintState[0], valueConstraintState[0]);	
		
		return Optional.of(primeRefs);
	}
}
	