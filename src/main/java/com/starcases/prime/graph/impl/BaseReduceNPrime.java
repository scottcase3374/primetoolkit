package com.starcases.prime.graph.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

// 
// start 1
//       2
//       3 <-  2 + 1
//       5 <-  3 + 2
//       7 <-  5 + 2
//       11 <- 7+3+1; 5+3+2+1
@Log
public class BaseReduceNPrime extends PrimeGrapher implements LogGraphIntfc
{
	static Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));
	
	public BaseReduceNPrime(PrimeSourceIntfc ps)
	{
		super(ps,log);
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
	
	@Override
	public void log()
	{
		log(2);
	}
	
	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	public void log(int maxReduce)
	{
		for (int i = 0; i< ps.getMaxIdx(); i++)
		{ 
			PrimeRefIntfc pr;				
			try
			{
				ArrayList<Integer> ret = new ArrayList<>();
				pr = ps.getPrimeRef(i);
				primeReduction(i, reducer.apply(maxReduce, ret));
				int [] tmpI = {0};			
				System.out.println(String.format("Prime [%d] %s", pr.getPrime(), 
						ret.stream().map(idx -> String.format("base-%d-count:[%d]", ps.getPrime(tmpI[0]++), idx)).collect(Collectors.joining(", "))));
			}
			catch(Exception e)
			{
				log.severe("Error: " + e);
				break;
			}				
		}	
	}
}
