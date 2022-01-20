package com.starcases.prime.base.triples;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import javax.validation.constraints.Min;

import com.starcases.prime.base.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;

/*
 *  Given a prime, find every set of 3 pre-existing primes that sum to the prime.
 *
 *
 *  A few examples of a single triple (or 2) per prime.
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
 * 27 103-> 19,41,43
 * 28 107->
 * 29 109-> 31,37,41 *
 */
@Log
public class BaseReduceTriple extends AbstractPrimeBaseGenerator
{
	@NonNull
	static final Comparator<String> nodeComparator = (o1,o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));

	@NonNull
	BaseTypes activeBaseId;

	@Min(0)
	static AtomicInteger good = new AtomicInteger(0);

	/**
	 * Constructor
	 *
	 * @param ps
	 */
	public BaseReduceTriple(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);

		activeBaseId = BaseTypes.THREETRIPLE;
		ps.setActiveBaseId(activeBaseId);
	}

	/**
	 * Main method responsible for producing the triples for a single prime.
	 * @param prime
	 */
	private void reducePrime(@NonNull PrimeRefIntfc prime)
	{
		AllTriples triple = new AllTriples(ps, prime);
		triple.process();
	}

	/**
	 * top-level function; iterate over entire dataset to reduce every prime
	 * @param maxReduce
	 */
	public void genBases()
	{
		AtomicInteger counter = new AtomicInteger(0);
		if (doLog)
			log.entering("BaseReduce3Triple", "genBases()");

		BigInteger seven = BigInteger.valueOf(7L);
		final var minPrimeIdx = ps.getPrimeIdx(seven);

		Iterator<PrimeRefIntfc> pRefIt = ps.getPrimeRefIter();

		// handle Bootstrap values - can't really represent < 11 with a sum of 3 primes
		while(pRefIt.hasNext())
		{
			var curPrime = pRefIt.next();
			var bNew = new BitSet();
			bNew.set(counter.incrementAndGet());

			curPrime.getPrimeBaseData().addPrimeBase(bNew, BaseTypes.THREETRIPLE);
			if (curPrime.getPrimeRefIdx() == minPrimeIdx)
				break;
		}

		ExecutorService workStealingPool = Executors.newWorkStealingPool(100);
		List<CompletableFuture<String>> futures = new ArrayList<>();

		// Process the values which can be represented by the sum of 3 primes.
		while (pRefIt.hasNext())
		{
			var curPrime = pRefIt.next();

			CompletableFuture<String> compFuture = new CompletableFuture<>();
			futures.add(compFuture);

			compFuture.completeAsync(() ->  handlePrime(curPrime, counter.incrementAndGet()) , workStealingPool);
		}

		long [] c = {0};
		futures.stream().map(CompletableFuture::join).forEach(x ->  c[0]++);

		if (log.isLoggable(Level.INFO))
			log.info(String.format("Total valid entries: %d out of %d; completed(%d)",  + good.get(), ps.getMaxIdx(),c[0]));
	}


	String handlePrime(PrimeRefIntfc curPrime, int counter)
	{
		var retVal = String.format("p[%d]idx[%d] good=", curPrime.getPrime(), counter);
		try
		{
			reducePrime(curPrime);
			good.getAndIncrement();
			return retVal + good.get();
		}
		catch(Exception e)
		{
			log.severe(String.format("BaseReduce3Triple generation => idx[%d] prime [%d] error: %s", counter, curPrime.getPrime(), e.toString()));
			e.printStackTrace();
		}
		return retVal + "false";
	}
}
