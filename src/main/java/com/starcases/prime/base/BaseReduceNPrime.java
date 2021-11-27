package com.starcases.prime.base;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.starcases.prime.impl.AbstractPrimeBase;
import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;

// 
// start 1
//       2
//       3 <-  2 + 1
//       5 <-  3 + 2
//       7 <-  5 + 2
//       11 <- 7+3+1; 5+3+2+1
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
	final BiFunction<Integer, ArrayList<Integer>, Consumer<Integer>> reducer = (m, a)-> idx -> 
	{
		if (idx < m)
		{
			while (m > a.size())
				a.add(0);
			
			a.set(idx, a.get(idx)+1);
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
	private void primeReduction(Integer idx, Consumer<Integer> reducer)
	{	
		ps.getPrimeRef(idx)
		.getPrimeBaseIdxs()
		.stream()
		.boxed()
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
			log.info(String.format("genBases(): maxReduce[%d]", maxReduce));
		}
		
		for (int i = 0; i< ps.getMaxIdx(); i++)
		{ 
			PrimeRefIntfc pr;				
			try
			{
				ArrayList<Integer> ret = new ArrayList<>();
				pr = ps.getPrimeRef(i);
				primeReduction(i, reducer.apply(maxReduce, ret));
				int [] tmpI = {0};			
				if (doLog) 
					log.info(String.format("Prime [%d] %s", pr.getPrime(), 
						ret.stream().map(idx -> String.format("base-%d-count:[%d]", ps.getPrime(tmpI[0]++), idx)).collect(Collectors.joining(", "))));
				BitSet bs = new BitSet();
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
