package com.starcases.prime.base;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Stack;
import java.util.function.Function;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Optional;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;

enum QueueOp { ADD, SUB, SET }

@Log
@NoArgsConstructor
class Triple
{
	enum ConstraintState { MATCH, LOW, HIGH, DUPE, MISSING_BASE, NOT_PRIME };
	PrimeRefIntfc targetPrime;
	PrimeSourceIntfc ps;
	Function<PrimeRefIntfc, PrimeRefIntfc> initBase;
	
	@Getter
	PrimeRefIntfc [] primeRefs = new PrimeRefIntfc[3];
	Stack<PrimeRefIntfc[]> backTrackStack = new Stack<>();
	ArrayDeque<QueueRequest> qRequest = new ArrayDeque<>();
	
	
		
	Triple(PrimeSourceIntfc ps, PrimeRefIntfc targetPrime, Function<PrimeRefIntfc, PrimeRefIntfc> initBase)
	{
		this.ps = ps;
		this.targetPrime = targetPrime;
		this.initBase = initBase;
		
		newRequest(new QueueRequest(QueueOp.SET, initBase.apply(targetPrime), TripleIdx.TOP));
		newRequest(new QueueRequest(QueueOp.SET, ps.getPrimeRef(BigInteger.ONE).get(), TripleIdx.BOT));
		
		
		
	}
	
	/**
	 * unconditionally set the prime ref and prime int to the provided data.
	 * 
	 * This is an initialization item.
	 * 
	 * @param req
	 */
	void setVal(QueueRequest req)
	{
		// Update prime ref for target idx 
		primeRefs[req.idx().ordinal()] = req.prime();
	}
	
	ConstraintState ensureConstraints(Consumer<QueueRequest> consumer, QueueRequest req)
	{
		ConstraintState  beforeState = checkConstraints(); 
		if (ConstraintState.MISSING_BASE != beforeState)
			saveVals(primeRefs);
		
		consumer.accept(req);

		ConstraintState afterState = checkConstraints();
		return afterState;
	}
	 
	ConstraintState checkConstraints()
	{
		ConstraintState cs = ConstraintState.MATCH;
		
		final int numBases = primeRefs.length;
		final boolean baseCountOk = numBases == 3;
		if (!baseCountOk)
			cs = ConstraintState.MISSING_BASE;
		else
		{
			final long distinctBases = Arrays.asList(primeRefs).stream().distinct().count();
			final boolean distinctBaseCountOk = distinctBases == 3;
			if (!distinctBaseCountOk)
				cs = ConstraintState.DUPE;
			else
			{
				final BigInteger targetPrimeVal = targetPrime.getPrime();
				final BigInteger sum = Arrays.asList(primeRefs).stream().filter(Objects::nonNull).distinct().map(p -> p.getPrime()).reduce(BigInteger.ZERO, BigInteger::add);
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

	
	/**
	 * if the prime int set meets constraints after adjusting values as needed; 
	 * set the prime ref array to the refs that match the valid set of sum'ed primes.
	 */
	void adjustPrimeRefs()
	{
		BigInteger diff = tgtP().subtract(topP().add(botP()));
		
		// Goal is assign mid value
		if (topP().compareTo(diff) == -1)
		{
			// swap cur top / mid
			swapR(TripleIdx.TOP, TripleIdx.MID);
			
			log("aft swapR", sum(), diff);
			
			Optional<PrimeRefIntfc> maybePrime = ps.getPrimeRef(diff);
			if (maybePrime.isPresent())
			{
				// set top to new top PRIME
				topR(maybePrime.get());
			}
			else // diff is a prime in itself so must split between the items.
			{
				// diff was negative so picks lower 
				Optional<PrimeRefIntfc> tmpPr = ps.getNearPrimeRef(diff);
				if (tmpPr.isPresent())
				{
					topR(tmpPr.get());
					// Determine how far off our new top prime is from the original diff
					BigInteger tmpP = topP().subtract(diff);
					
					Optional<PrimeRefIntfc> tmpPr2 = ps.getNearPrimeRef(botP().add(tmpP));
					if (tmpPr2.isPresent())
					{
						botR(tmpPr2.get());
					}
				}
			}
		}
		
		log("exit process", sum(), diff);
	}
	
	BigInteger sum()
	{
		return Arrays.asList(primeRefs).stream().filter(Objects::nonNull).map(PrimeRefIntfc::getPrime).reduce(BigInteger.ZERO, BigInteger::add);
	}
	
	void log(String loc, BigInteger sum, BigInteger diff )
	{
		log.info(String.format("process %s: prime:[%d]  sum:[%d]  vals:[%s]  sum-prime-diff:[%d]",
				loc,
				targetPrime.getPrime(), 
				sum, 
				Arrays.asList(primeRefs).stream().filter(Objects::nonNull).map(Object::toString)
				.collect(Collectors.joining(",", "[", "]")),
				diff));
	}
	
	void add(QueueRequest req)
	{
		// Update prime ref for target idx 
		primeRefs[req.idx().ordinal()] = req.prime();		
	}
	
	void sub(QueueRequest req)
	{
		
	}
	
	public void newRequest(QueueRequest req)
	{
		qRequest.add(req);	
		
	}
	
	public PrimeRefIntfc[] process()
	{	
		qRequest.stream().forEach(q -> 	
									{ 
										switch (q.qop())
										{
										case ADD:
											ensureConstraints(this::add, q);
											break;
											
										case SUB:
											ensureConstraints(this::sub, q);
											break;
											
										case SET:
											ensureConstraints(this::setVal, q);
											break;
										}
									}
								);
		
		adjustPrimeRefs();

		//BigInteger sum = primeIntSet.stream().reduce(BigInteger::add).orElse(BigInteger.ZERO);
		return primeRefs;
		
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
	
	private void clearVals()
	{
		backTrackStack.pop();
	}
}
/*
 *  Given a prime, find a bitset representing 3 pre-existing primes that sum to the prime. 
 *  
 *  Algorithm TRIES to avoid use of the initial values as bases- 1, 2, 3.
 *  
 *  For example (not required responses but valid).
 *     P     B
 *	   R     A
 *  I  I     S
 *  D  M     E
 *  X  E     S
 *  
 *  0  1      x
 *  1  2      x
 *  2  3      x
 *  3  5      x 
 *  4  7	  x
 *  5 11 -> 1,3,7
 *  6 13 -> 1,5,7
 *  7 17 -> 1,5,11
 *  8 19 -> 1,7,11
 *  9 23 -> 5,7,11 *  [* note contiguous primes];   3,7,13
 * 10 29 -> 1,11,17; 5,11,13
 * 11 31 -> 7,11,13 *
 * 12 37 -> 7,13,17
 * 13 41 -> 11,13,17 *;  5,13,23
 * 14 43 -> 7,17,19
 * 15 47 -> 7,17,23; 5,19,23
 * 16 53 -> 11,19,23
 * 17 59 -> 17,19,23 *
 * 18 61 -> 1,29,31
 * 19 67 -> 7,29,31
 * 20 71 -> 11,29,31
 * 21 73 -> 13,29,31
 * 22 79 -> 19,29,31
 * 23 83 -> 23,29,31 *
 * 24 89 -> 19,29,41
 * 25 97 -> 13,41,43; 19,37,41
 * 26 101-> 23,37,41
 * 27 103->
 * 28 107-> 
 * 29 109-> 31,37,41 *	
 */
@Log
public class BaseReduceTriple extends AbstractPrimeBase
{
	static final Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));

	BaseTypes activeBaseId;
	
	public BaseReduceTriple(PrimeSourceIntfc ps)
	{
		super(ps, log);
		
		if (!ps.baseExist(BaseTypes.DEFAULT))
			(new BaseReduceNPrime(ps)).genBases();
		
		activeBaseId = BaseTypes.THREETRIPLE;
		ps.setActiveBaseId(activeBaseId);
	}
	
	private void reducePrime(PrimeRefIntfc prime)
	{	
		Triple triple = 
				new Triple(
						ps, 
						prime, 
						p1 -> 
							{ 
								var multip = "0.4";  
								return ps.getNearPrimeRef((new BigDecimal(multip)).multiply(new BigDecimal(prime.getPrime()))).get(); 
							});
		PrimeRefIntfc [] vals = triple.process();
		BigInteger sum = Arrays.asList(vals).stream().filter(Objects::nonNull).map(PrimeRefIntfc::getPrime).reduce(BigInteger.ZERO, BigInteger::add);
		log.warning(String.format("prime %d == sum %d: %s", prime.getPrime(), sum, prime.getPrime().compareTo(sum) == 0));
		addPrimeBases(prime, vals);
	}

	private void addPrimeBases(PrimeRefIntfc prime, PrimeRefIntfc...vals)
	{
		var bs = new BitSet();
		Arrays.asList(vals).stream().filter(Objects::nonNull).map(PrimeRefIntfc::getPrimeRefIdx).forEach(bs::set);
		//for (var p : vals)
	//	{
		//	bs.set(p.getPrimeRefIdx());
	//	}
		prime.addPrimeBase(bs, BaseTypes.THREETRIPLE);
	}
	
	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	public void genBases()
	{
		int counter = 0;
		if (doLog)
			log.entering("BaseReduce3Triple", "genBases()");
		
		BigInteger seven = BigInteger.valueOf(7L);
		final var minPrimeIdx = ps.getPrimeIdx(seven);
		
		Iterator<PrimeRefIntfc> pRefIt = ps.getPrimeRefIter();
		
		// handle Bootstrap values - can't really represent < 11 with a sum of 3 primes
		while(pRefIt.hasNext())
		{
			var curPrime = pRefIt.next();
			counter++;
			var bNew = new BitSet();
			bNew.set(0);
			curPrime.addPrimeBase(bNew, BaseTypes.THREETRIPLE);
			if (curPrime.getPrimeRefIdx() == minPrimeIdx)
				break;
		}
		
		// Process
		while (pRefIt.hasNext()) 
		{ 
			var curPrime = pRefIt.next();
			counter++;
			try
			{
				reducePrime(curPrime);
			}
			catch(Exception e)
			{
				log.severe(String.format("BaseReduce3Triple generation => idx[%d] prime [%d] error: %s", counter, curPrime.getPrime(), e.toString()));
				e.printStackTrace();
				break;
			}				
		}
	}	
}
