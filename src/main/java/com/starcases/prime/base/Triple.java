package com.starcases.prime.base;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

enum TripleIdx { TOP, MID, BOT}

@Log
@NoArgsConstructor
class Triple
{
	protected static int good = 0;
	enum ConstraintState { MATCH, LOW, HIGH, DUPE, MISSING_BASE, NOT_PRIME}
	PrimeRefIntfc targetPrime;
	PrimeSourceIntfc ps;
	Function<PrimeRefIntfc, PrimeRefIntfc> initTop;
	Function<PrimeRefIntfc, PrimeRefIntfc> initBot;

	// After initial selection of a high and low value, execution of a subset of these results in 
	// generation of a valid combination of 3 primes that sum to the original prime.
	List<Function<Optional<PrimeRefIntfc>, Optional<ConstraintState>>> processFuncs = new ArrayList<>(); 
	
	static final EnumSet<ConstraintState> INVALID_STATES = EnumSet.of(ConstraintState.HIGH, ConstraintState.DUPE, ConstraintState.NOT_PRIME);
	
	@Getter
	PrimeRefIntfc [] primeRefs = new PrimeRefIntfc[3];
	Deque<PrimeRefIntfc[]> backTrackStack = new ArrayDeque<>();
	
	void dumpStack(String loc)
	{
		log.info(String.format("## %s ## **** stack dump **** prime[%d] ", loc, targetPrime.getPrime()));
		backTrackStack.stream().forEach(pa -> System.out.println(Arrays.asList(pa)
				.stream().filter(Objects::nonNull).map(o -> o.getPrime().toString() ).collect(Collectors.joining(","))));
	}
	
	/**
	 * when finding an initial mid prime from a diff from tgt prime and current sum, if the resulting prime doesn't
	 * match the diff then handle the remainder here.  This should not preclude the
	 * assignment of the mid value - i.e. a gap must still remain between top/bot.
	 */
	Function<Optional<PrimeRefIntfc>, Optional<ConstraintState>> procExtra = tmpMid ->
	{
		final String func = "procExtra()";
		lenter(func, null);
		if (tmpMid.isPresent()) 
		{
			BigInteger tmpDiff = tgtP().subtract(sum());
			if (!tmpMid.get().getPrime().equals(tmpDiff))
			{
				BigInteger additionalOffset = tmpDiff.subtract(tmpMid.get().getPrime());
				return processPrimeSumOffset(additionalOffset);
			}						
		}
		return Optional.empty();
	};
	
	Function<Optional<PrimeRefIntfc>, Optional<ConstraintState>> procMidNoDupe = tmpMid ->
	{
		final String func = "procMidNoDupe()";
		lenter(func, null);
		if (tmpMid.isPresent()
				&& findDupeBaseIdx(tmpMid.get()).isEmpty()) 
		{		
			// Set 
			//		mid as specified 
			// if no dupe is created.
			// mid is exact value needed.
			return Optional.of(ensureConstraints(func, this::setVal, tmpMid.get(), TripleIdx.MID));							
		}
		return Optional.empty();
	};
	
/*	Function<Optional<PrimeRefIntfc>, Optional<ConstraintState>> procDupe = tmpMid ->
	{
		if (tmpMid.isPresent()
				&& findDupeBaseIdx(tmpMid.get()).isPresent()) 
		{		
			// mid duped
			return Optional.of(ensureConstraints(this::setVal, tmpMid.get(), TripleIdx.MID));							
		}
		return Optional.empty();
	};*/
	
	Function<Optional<PrimeRefIntfc>, Optional<ConstraintState>> procTopUpMidDown = tmpMid ->
	{
		final String func = "procTopUpMidDown()";
		lenter(func, null);
		if (tmpMid.isPresent()
				&& tmpMid.get().getPrevPrimeRef().isPresent()
				&& findDupeBaseIdx(tmpMid.get().getPrevPrimeRef().get()).isEmpty()
				&& topR().getDistToNextPrime().get().add(tmpMid.get().getDistToPrevPrime().get()).equals(BigInteger.ZERO)) 
		{
			// move 
			//  top up
			//  mid down
			//
			// explicitly confirm that new mid doesn't dupe low.
			ensureConstraints(func, this::setVal, tmpMid.get().getPrevPrimeRef().get(), TripleIdx.MID);
			return Optional.of(ensureConstraints(func, this::setVal, topR().getNextPrimeRef().get(), TripleIdx.TOP));							
		}
		return Optional.empty();
	};
	
	Function<Optional<PrimeRefIntfc>, Optional<ConstraintState>> procMidDownBotUp = tmpMid ->
	{
		final String func = "procMidDownBotUp()";
		lenter(func, null);
		if (tmpMid.isPresent() 
				&& !tmpMid.get().getPrevPrimeRef().equals(botR().getNextPrimeRef())
				&& tmpMid.get().getDistToPrevPrime().isPresent()
				&& botR().getDistToNextPrime().isPresent()
				&& tmpMid.get().getDistToPrevPrime().get().add(botR().getDistToNextPrime().get()).equals(BigInteger.ZERO))
		{
			// Move 
			//    mid down
			//    bot up 
			// if dupe not produced.
			ensureConstraints(func, this::setVal, tmpMid.get().getPrevPrimeRef().get(), TripleIdx.MID);
			return Optional.of(ensureConstraints(func, this::setVal, botR().getNextPrimeRef().get(), TripleIdx.BOT));							
		}
		return Optional.empty();
	};
	
	Function<Optional<PrimeRefIntfc>, Optional<ConstraintState>>  mid4 =  tmpMid ->
	{
		final String func = "mid4()";
		lenter(func, null);
		// mid should already be set so any mid value here is a partial offset of some sort.
		//
		if (tmpMid.isPresent()) 
		{
			// Move
			//   bot partly down
			//   add mid + extra from bot to top
			// if dupe not produced
			ensureConstraints(func, this::setVal, ps.getPrimeRef(botP().subtract(tmpMid.get().getPrime())).get(), TripleIdx.BOT);
			return Optional.of(
					ensureConstraints(func, this::setVal, 
										ps.getPrimeRef(topP().add(tmpMid.get().getPrime()).add(tmpMid.get().getPrime())).get(), 
										TripleIdx.TOP));
		}
		return Optional.empty();
	};
	
	Function<Optional<PrimeRefIntfc>, Optional<ConstraintState>> procTopUpBotDownMidSet = tmpMid ->
	{
		final String func = "procTopUpBotDownMidSet()";
		lenter(func, null);
		if (tmpMid.isPresent() 
				//&& !botR().getPrevPrimeRef().equals(tmpMid)
				&& topR().getDistToNextPrime().get().add(botR().getDistToPrevPrime().get()).equals(BigInteger.ZERO))
		{
			// adjust all 3
			//   top up
			//   bot down
			//   
			//   mid to expected value
			// 
			// ensure mid != bot
			ensureConstraints(func, this::setVal, botR().getPrevPrimeRef().get(), TripleIdx.BOT);
			ensureConstraints(func, this::setVal, topR().getNextPrimeRef().get(), TripleIdx.TOP);							
			return Optional.of(ensureConstraints(func, this::setVal, tmpMid.get(), TripleIdx.MID));
		}
		return Optional.empty();
	};
	
	Function<Optional<PrimeRefIntfc>, Optional<ConstraintState>> mid6 = tmpMid ->
	{
		final String func = "mid6()";
		lenter(func, null);
		if (tmpMid.isPresent() 
				&&  topR().getPrimeRefIdx() - botR().getPrimeRefIdx() <= 1)
		{
			return Optional.of(ensureConstraints(func, this::setVal, topR().getNextPrimeRef().get(), TripleIdx.TOP));										
		}
		return Optional.empty();
	};	

	Triple(PrimeSourceIntfc ps, PrimeRefIntfc targetPrime, UnaryOperator<PrimeRefIntfc> initTop, UnaryOperator<PrimeRefIntfc> initBot)
	{
		this.ps = ps;
		this.targetPrime = targetPrime;
		this.initTop = initTop;		
		this.initBot = initBot;
		
		processFuncs.add(procMidNoDupe);
		processFuncs.add(procTopUpBotDownMidSet);
		processFuncs.add(procTopUpMidDown);
		processFuncs.add(procMidDownBotUp);
		
		//processFuncs.add(procExtra);
		//processFuncs.add(mid4);
		//processFuncs.add(mid6);
	}
	
	/**
	 * unconditionally set the prime ref and prime int to the provided data.
	 * 
	 * This is an initialization item.
	 * 
	 * @param req
	 */
	void setVal(PrimeRefIntfc prime, TripleIdx idx)
	{
		// Update prime ref for target idx 
		primeRefs[idx.ordinal()] = prime;
	}
	
	ConstraintState ensureConstraints(final String func, BiConsumer<PrimeRefIntfc, TripleIdx> consumer, PrimeRefIntfc prime, TripleIdx idx)
	{
		lenter(func, null);
		saveVals(primeRefs);
		
		consumer.accept(prime, idx);

		ConstraintState afterState = checkConstraints();

		lexit(func, afterState);
		if (INVALID_STATES.contains(afterState))
		{
			log.severe("####### Reverted saved vals:" + func + "; state: " + afterState.toString());
			revertVals(primeRefs);
		}
		return afterState;
	}
	 
	ConstraintState checkConstraints()
	{
		ConstraintState cs;
		
		final long numBases = Arrays.asList(primeRefs).stream().filter(Objects::nonNull).count();
		final boolean baseCountOk = numBases == 3;
		if (!baseCountOk)
		{
			cs = ConstraintState.MISSING_BASE;
		}		
		else
		{	
			final long distinctBases = Arrays.asList(primeRefs).stream().filter(Objects::nonNull).distinct().count();
			final boolean distinctBaseCountOk = distinctBases == 3;
			if (!distinctBaseCountOk)
			{
				cs = ConstraintState.DUPE;
			}
			else
			{
				final BigInteger targetPrimeVal = targetPrime.getPrime();
				final BigInteger sum = Arrays.asList(primeRefs).stream().filter(Objects::nonNull).distinct().map(PrimeRefIntfc::getPrime).reduce(BigInteger.ZERO, BigInteger::add);
				final int sumCompared = sum.compareTo(targetPrimeVal);
				cs = switch (sumCompared)
						{
						case -1 -> ConstraintState.LOW;
						case 1  -> ConstraintState.HIGH;
						default -> ConstraintState.MATCH;
						};
			}
		}
			
		return cs;
	}

	/**
	 * Determine if prime duplicates existing bases.
	 */
	Optional<TripleIdx> findDupeBaseIdx(PrimeRefIntfc prime)
	{		
		for (int i=0; i < primeRefs.length; i++)
			if (primeRefs[i] != null && primeRefs[i].getPrime().equals(prime.getPrime()))
				return Optional.of(TripleIdx.values()[i]);
		return Optional.empty();
	}
	
	// returns top prime reference
	PrimeRefIntfc topR()
	{
		return this.primeRefs[TripleIdx.TOP.ordinal()]; 
	}

	// returns top prime integer
	BigInteger topP()
	{
		return topR().getPrime();
	}

	// Swap top reference to reference from idx
	void swapR(TripleIdx idx1, TripleIdx idx2)
	{
		PrimeRefIntfc tmp  = primeRefs[idx1.ordinal()]; 
		primeRefs[idx1.ordinal()] =  primeRefs[idx2.ordinal()];
		primeRefs[idx2.ordinal()] = tmp;
	}
	
	void topR(PrimeRefIntfc ref)
	{
		primeRefs[TripleIdx.TOP.ordinal()] = ref;
	}
	
	// returns mid prime reference
	PrimeRefIntfc midR()
	{
		return this.primeRefs[TripleIdx.MID.ordinal()]; 
	}
	
	void midR(PrimeRefIntfc ref)
	{
		primeRefs[TripleIdx.MID.ordinal()] = ref;
	}

	// returns mid prime integer
	BigInteger midP()
	{
		return midR().getPrime();
	}
	
	// returns bottom prime ref
	PrimeRefIntfc botR()
	{
		return this.primeRefs[TripleIdx.BOT.ordinal()]; 
	}

	void botR(PrimeRefIntfc ref)
	{
		primeRefs[TripleIdx.BOT.ordinal()] = ref;
	}

	// return bottom prime integer
	BigInteger botP()
	{
		return botR().getPrime();
	}

	BigInteger tgtP()
	{
		return this.targetPrime.getPrime();
	}

	// substract the idx values of the primes
	//  I would like to avoid ties to indexes but it does simplify
	//  the determination/check of whether/how many items exist between 2 primes.
	public int idxSub(PrimeRefIntfc a, PrimeRefIntfc b)
	{
		return a.getPrimeRefIdx()-b.getPrimeRefIdx();
	}
	
	public BigInteger sum()
	{
		return Arrays.asList(primeRefs).stream().filter(Objects::nonNull).map(PrimeRefIntfc::getPrime).reduce(BigInteger.ZERO, BigInteger::add);
	}
	
	void log(String loc, ConstraintState state )
	{
		log.info(String.format("%s : prime:[%d]  sum:[%d]  vals:[%s]  sum-prime-diff:[%d] constraint-state[%s]",
				loc,
				targetPrime.getPrime(), 
				sum(), 
				Arrays.asList(primeRefs).stream().filter(Objects::nonNull).map(Object::toString)
				.collect(Collectors.joining(",", "[", "]")),
				tgtP().subtract(sum()),
				state != null ? state.toString() : "<constraint state unset>"));
		
		if (ConstraintState.MATCH != state && state != null)
			dumpStack(loc);
	}

	void lenter(String loc, ConstraintState state)
	{
		log("Handling item for " + loc, state);
	}
	
	void lexit(String loc, ConstraintState state)
	{
		log("Completed item for " + loc, state);
	}
	
	protected Optional<ConstraintState> processPrimeSumOffset(BigInteger additionalOffset)
	{
		final String func = "processPrimeSumOffset()";
		lenter(func, null);
		
		int topMinusBotIdx = topR().getPrimeRefIdx() - botR().getPrimeRefIdx();					
		Optional<BigInteger> topDistPrev = topR().getDistToPrevPrime();
		Optional<BigInteger> topDistNext = topR().getDistToNextPrime();
		
		Optional<BigInteger> botDistNext = botR().getDistToNextPrime();
		Optional<BigInteger> botDistPrev = botR().getDistToPrevPrime();
	
		ConstraintState cs = null;
		
		if (additionalOffset.signum() == 1)
		{				
			if (topDistNext.get().compareTo(additionalOffset) == 0)
			{
				// case 1
				//
				// can increase top to match added offset
				cs = ensureConstraints(func, this::setVal, topR().getNextPrimeRef().get(), TripleIdx.TOP);
			}
			else if (findDupeBaseIdx(ps.getPrimeRef(botP().add(additionalOffset)).get()).isEmpty() 
					&& botDistNext.get().compareTo(additionalOffset) == 0)  
			{
				// case 2
				//
				// have 2+ gaps between primes if bringing bot closer to top (leaves loc for mid value)				
				//     Top, <available prime>, <available prime>, Bot
				// so raise Bot 
				cs = ensureConstraints(func, this::setVal, ps.getPrimeRef(botP().add(additionalOffset)).get(), TripleIdx.BOT);
			}			
			else if (topDistNext.get().add(botDistPrev.get()).equals(additionalOffset)) 
			{
				// case 3
				//
				// move different top/bot different offsets (apart) that cancel out all except
				// the additional offset.					
				ensureConstraints(func, this::setVal, botR().getPrevPrimeRef().get(), TripleIdx.BOT);
				cs = ensureConstraints(func, this::setVal, topR().getNextPrimeRef().get(), TripleIdx.TOP);	
			}							
			else if (topMinusBotIdx > 3 && topDistPrev.get().add(botDistNext.get()).equals(additionalOffset))
			{
				// case 
				//
				// move different top/bot different offsets (together) that cancel out all except
				// the additional offset and leaves open loc for mid value.
				ensureConstraints(func, this::setVal, topR().getPrevPrimeRef().get(), TripleIdx.TOP);
				cs = ensureConstraints(func, this::setVal, botR().getNextPrimeRef().get(), TripleIdx.BOT);									
			}
			else
			{
				log.severe(String.format("#### processPrimeSumOffset - didn't handle added positive offset: [%d]  - target prime[%d]", additionalOffset, targetPrime.getPrime()));
			}
		}
		else
		{
			if (botDistPrev.get().compareTo(additionalOffset) == 0)
			{
				// case 1
				//
				// can decr bot to match offset
				cs = ensureConstraints(func, this::setVal, botR().getPrevPrimeRef().get(), TripleIdx.BOT);
			}
			else if (topMinusBotIdx > 2 && topDistPrev.get().compareTo(additionalOffset) == 0)  
			{
				// case 2
				//
				// have 2+ gaps between primes if bringing bot closer to top (leaves loc for mid value)				
				//     Top, <available prime>, <available prime>, Bot
				// so lower top 
				cs = ensureConstraints(func, this::setVal, topR().getPrevPrimeRef().get(), TripleIdx.TOP);
			}			
			else if (topDistNext.get().add(botDistPrev.get()).equals(additionalOffset)) 
			{
				// case 3
				//
				// move different top/bot different offsets (apart) that cancel out all except
				// the additional offset.					
				ensureConstraints(func, this::setVal, botR().getPrevPrimeRef().get(), TripleIdx.BOT);
				cs = ensureConstraints(func, this::setVal, topR().getNextPrimeRef().get(), TripleIdx.TOP);	
			}							
			else if (topMinusBotIdx > 3 && topDistPrev.get().add(botDistNext.get()).equals(additionalOffset))
			{
				// case 4 
				//
				// move different top/bot different offsets (together) that cancel out all except
				// the additional offset and leaves open loc for mid value.
				ensureConstraints(func, this::setVal, topR().getPrevPrimeRef().get(), TripleIdx.TOP);
				cs = ensureConstraints(func, this::setVal, botR().getNextPrimeRef().get(), TripleIdx.BOT);									
			}
			else
			{
				log.severe(String.format("#### processPrimeSumOffset - didn't handle added negative offset: [%d]  - target prime[%d]", additionalOffset, targetPrime.getPrime()));
			}
		}
		lexit(func, cs);
		return Optional.ofNullable(cs);		
	}
	
	public Optional<PrimeRefIntfc[]> process()
	{	
		final String func = "process()";
		lenter(func, null);
		ensureConstraints(func, this::setVal, initTop.apply(targetPrime), TripleIdx.TOP);
		ConstraintState cs = ensureConstraints(func, this::setVal, initBot.apply(targetPrime), TripleIdx.BOT);

		//
		// if using the default init setup for top/bot, sum cannot exceed target prime yet.
		//
		//    so even if mid == diff
		//              it may or may not be a prime
		//              it could be dupe value
		
		// If diff value is also a prime value then it is a candidate
		// for being a base.
		
		// MUST set mid without causing dupe
					
		for (Function<Optional<PrimeRefIntfc>, Optional<ConstraintState>> fn : processFuncs)
		{
			Optional<PrimeRefIntfc> tmpMid = ps.getNearPrimeRef(tgtP().subtract(sum()));
			
			if (tmpMid.isEmpty())
				return Optional.empty();
	
			Optional<ConstraintState> ret = fn.apply(tmpMid);
			if (ret.isPresent())
			{
				cs = ret.get();
				
				if (ConstraintState.MATCH == cs)
					break;
			}
		}
		
		if (ConstraintState.MATCH != cs)
		{
			log(func, cs);	
		}
		else
		{
			Triple.good++;
		}
		
		return Optional.of(primeRefs);
	}
	
	private void saveVals(PrimeRefIntfc [] vals)
	{
		var saved = Arrays.copyOf(vals, vals.length);
		backTrackStack.push(saved);
	}
	
	private void revertVals(PrimeRefIntfc [] vals)
	{
		var saved = backTrackStack.pop();
		System.arraycopy(saved, 0, vals, 0, saved.length);
	}
}
	