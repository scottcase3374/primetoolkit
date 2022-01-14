package com.starcases.prime.base;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.Optional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;

/**
 * Note, this is output as part of the base generation and not part of the "log" directive.
 *
 *
 * @FIXME Since this uses some recursion it easily runs out of stack space; rework it to avoid problem.
 *
 * Given a prime and a maximum number of primes; reduce default bases to multiples
 * of the bases <= maximum prime provided.
 *
 * Example: For max base-prime of 2.
 *  Prime 43 -> bases 7,            17												 ,19
 *                   /\			 /--/------\					 					  /\----------------------\
 *                  2  5	    1  5        11                                       1  5          				13
 *                     /\         /\        /\---------\                                /\                       /\-----------\
 *                    2	 3       2  3      1  3         7                              2  3                     1  5           7
 *                       /\         /\        /\        /\                                /\                       /\          /\
 *                      1  2        1 2      1  2      2  5                               1 2                     2  3        2  5
 *                                                        /\                                                         /\          /\
 *                                                       2  3                                                       1  2        2  3
 *                                                          /\                                                                     /\
 *                                                         1  2                                                                   1  2
 *
 * so result would be: 2(x3) 1(x1),  1(x5) 2(x6),      1(x5) 2(x7)
 *          which reduces to:  1(x11), 2(x16)   =>  1x11 + 2x16 = 43
 */
@Log
public class BaseReduceNPrime extends AbstractPrimeBase
{
	static final Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));

	@Min(2)
	@Max(3)
	private int maxReduce;

	public BaseReduceNPrime(@NonNull PrimeSourceIntfc ps)
	{
		super(ps,log);
	}

	public void setMaxReduce(@Min(2) @Max(3) int maxReduce)
	{
		this.maxReduce = maxReduce;
	}

	/*
	 * m  the highest (non-inclusive) index to reduce to.
	 * a  arraylist of current result indexes shared throughout the call chain
	 * idx the current index being processed
	 */
	final BiFunction<Integer, ArrayList<Integer>, Consumer<PrimeRefIntfc>> fnReducer = (m, a)-> idx ->
	{
		if (idx.getPrimeRefIdx() < m)
		{
			while (m > a.size())
				a.add(0);

			a.set(idx.getPrimeRefIdx(), a.get(idx.getPrimeRefIdx())+1);
		}
		else
		{
			this.primeReduction(idx, this.fnReducer.apply(m, a));
		}
	};

	/**
	 *
	 * @param idx idx current index to reduce;
	 * @param reducer function implementing reduction algo
	 */
	private void primeReduction(@NonNull PrimeRefIntfc primeRef, @NonNull Consumer<PrimeRefIntfc> reducer)
	{
		primeRef
		.getPrimeBaseIdxs()
		.get(0)
		.stream()
		.boxed()
		.map(i ->  ps.getPrimeRef(i))
		.filter(Optional::isPresent)
		.map(Optional::get)
		.forEach(reducer);
	}

	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	public void genBases()
	{
		if (doLog)
		{
			log.entering("BaseReduceNPrime", "genBases()");

			if (log.isLoggable(Level.INFO))
			{
				log.info(String.format("genBases(): maxReduce[%d]", maxReduce));
			}
		}

		Iterator<PrimeRefIntfc> it = ps.getPrimeRefIter();
		while (it.hasNext())
		{
			PrimeRefIntfc pr = it.next();
			try
			{
				ArrayList<Integer> ret = new ArrayList<>();
				primeReduction(pr, fnReducer.apply(maxReduce, ret));
				int [] tmpI = {0};
				if (doLog && log.isLoggable(Level.INFO))
				{
					log.info(String.format("Prime [%d] %s", pr.getPrime(),
						ret.stream().map(idx -> String.format("base-%d-count:[%d]", ps.getPrime(tmpI[0]++).get(), idx)).collect(Collectors.joining(", "))));
				}
				var bs = new BitSet();
				ret.stream().forEach(bs::set);
				pr.addPrimeBase(bs, BaseTypes.NPRIME);
			}
			catch(Exception e)
			{
				log.severe("Error: " + e);
				break;
			}
		}
	}
}
