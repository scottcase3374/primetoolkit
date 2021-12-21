package com.starcases.prime.base;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.Iterator;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;

/**
 * Note, this is output as part of the base generation and not part of the "log" directive.  
 * 
 * @TODO Need to migrate this to
 * the "log" directive and support the distinction between "base" and "count".
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
	static Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));
	
	private int maxReduce;
	
	public BaseReduceNPrime(PrimeSourceIntfc ps)
	{
		super(ps,log);
	}
	
	public void setMaxReduce(int maxReduce)
	{
		this.maxReduce = maxReduce;
	}
	
	/*
	 * m  the highest (non-inclusive) index to reduce to.
	 * a  arraylist of current result indexes shared throughout the call chain
	 * idx the current index being processed
	 */
	final BiFunction<Integer, ArrayList<Integer>, Consumer<PrimeRefIntfc>> reducer = (m, a)-> idx -> 
	{
		if (idx.getPrimeRefIdx() < m)
		{
			while (m > a.size())
				a.add(0);
			
			a.set(idx.getPrimeRefIdx(), a.get(idx.getPrimeRefIdx())+1);
		}
		else 
		{ 
			this.primeReduction(idx, this.reducer.apply(m, a)); 
		}
	};
		 
	/**
	 *
	 * @param idx idx current index to reduce;
	 * @param reducer function implementing reduction algo
	 */
	private void primeReduction(PrimeRefIntfc primeRef, Consumer<PrimeRefIntfc> reducer)
	{	
		primeRef
		.getPrimeBaseIdxs()
		.stream()
		.boxed()
		.map(i ->  ps.getPrimeRef(i))
		.filter(o -> o.isPresent())
		.map(o -> o.get())
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
				primeReduction(pr, reducer.apply(maxReduce, ret));
				int [] tmpI = {0};			
				if (doLog && log.isLoggable(Level.INFO)) 
				{
					log.info(String.format("Prime [%d] %s", pr.getPrime(), 
						ret.stream().map(idx -> String.format("base-%d-count:[%d]", ps.getPrime(tmpI[0]++), idx)).collect(Collectors.joining(", "))));
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