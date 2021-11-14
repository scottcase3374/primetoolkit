package com.starcases.prime.graph.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.WindowConstants;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphBuilder;

import com.starcases.prime.graph.visualize.VisualizeGraph;
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
public class PrimeGrapher 
{
	private static Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));

	private PrimeSourceIntfc ps;
	
	private GraphBuilder<String, DefaultEdge, DefaultDirectedGraph<String, DefaultEdge>> primeGraph = new GraphBuilder<>(new DefaultDirectedGraph<>(DefaultEdge.class));
	
	private Graph<String,DefaultEdge> graph;

	public PrimeGrapher(PrimeSourceIntfc ps)
	{
		this.ps = ps;
		this.ps.init();
		this.populateData();
		this.graph = primeGraph.build();
	}
	
	public void setNodeLocations()
	{
		log.info("setNodeLocations() - start");
		graph
		.vertexSet()
		.stream()
		.sorted(nodeComparator)
		.forEach(n -> 
						{
							float inDegree = graph.inDegreeOf(n);
							float outDegree = graph.outDegreeOf(n);
							Integer id = Integer.decode(n);
							
							float x = 80 - (float)Math.sin(11 * id *127);
							float y = (inDegree * 11 + (float)Math.sin(id)) + (outDegree * 11 + (float)Math.cos(id)) ;
							float z = 0;
						//	n.setX(x);
						//	n.setY(y);
						//	n.setZ(z);
							log.info(String.format("Prime %s  x[%f] y[%f] z[%f]  in-degree[%f] out-degree[%f]", n, x,y,z, inDegree, outDegree ));
						
						});		
		log.info("setNodeLocations() - exit");
}
	
	@Command
	public void logGraphStructure()
	{
		System.out.println("log structure");
			graph
				.vertexSet()				
				.stream()
				.sorted(nodeComparator)
				.forEach(n -> 
							System.out.println(String.format("Prime %s: created-from:[count(%d), %s] creates-primes:[count(%d), %s]", 
						n, 
						graph.inDegreeOf(n),
						graph.incomingEdgesOf(n).stream().map(e -> graph.getEdgeSource(e)).collect(Collectors.joining(",")),
						graph.outDegreeOf(n),
						graph.outgoingEdgesOf(n).stream().map(e -> graph.getEdgeTarget(e)).collect(Collectors.joining(",")))));		
	}
	
	public void logNodeStructure()
	{
		try
		{
			int i = 0;
			while(true) 
			{ 
				PrimeRefIntfc ref = ps.getPrimeRef(i++);
				System.out.println(String.format("Prime %d bases %s", ref.getPrime(), ref.getIdxPrimes()));
			}
		}
		catch(Exception e)
		{
			log.severe("Exception:" + e);
		}
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
	public void logReduced(int maxReduce)
	{
		int i = 0;
		while(true) 
		{ 
			PrimeRefIntfc pr;				
			try
			{
				ArrayList<Integer> ret = new ArrayList<>();
				pr = ps.getPrimeRef(i);
				primeReduction(i++, reducer.apply(maxReduce, ret));
				int [] tmpI = {0};			
				System.out.println(String.format("Prime [%d] %s", pr.getPrime(), 
						ret.stream().map(idx -> String.format("base-%d-count:[%d]", ps.getPrime(tmpI[0]++), idx)).collect(Collectors.joining(", "))));
			}
			catch(Exception e)
			{
				break;
			}				
		}	
	}
	
	/*
	 * 
	 *  given prime-idx, bitset
	 *  
	 *  find new bitset where bitset.size()-1 is reduced to the sum of
	 *  3 pre-existing primes.
	 *     P     B
	 *	   R     A
	 *  I  I     S
	 *  D  M     E
	 *  X  E     S
	 *  
	 *  0 11 -> 1,3,7
	 *  1 13 -> 1,5,7
	 *  2 17 -> 1,5,11
	 *  3 19 -> 1,7,11
	 *  4 23 -> 5,7,11 *  <[*contiguous primes];   3,7,13
	 *  5 29 -> 1,11,17; 5,11,13
	 *  6 31 -> 7,11,13 *
	 *  7 37 -> 7,13,17
	 *  8 41 -> 11,13,17 *;  5,13,23
	 *  9 43 -> 7,17,19
	 * 10 47 -> 7,17,23; 5,19,23
	 * 11 53 -> 11,19,23
	 * 12 59 -> 17,19,23 *
	 * 13 61 -> 1,29,31
	 * 14 67 -> 7,29,31
	 * 15 71 -> 11,29,31
	 * 16 73 -> 113,29,31
	 * 17 79 -> 19,29,31
	 * 18 83 -> 23,29,31 *
	 * 19 89 -> 19,29,41
	 * 20 97 -> 13,41,43; 19,37,41
	 * 21 101-> 23,37,41
	 * 22 109-> 31,37,41 *	
	 */
	final BiFunction<Integer, BitSet, Consumer<BitSet>> treeReducer = (primeIdx, bs)-> idx -> 
	{
		Integer targetPrimeIdx = primeIdx;
		BigInteger targetPrime = ps.getPrime(targetPrimeIdx);
		if (bs.cardinality() != 3)
		{			
			Integer spIdx = ps.getNextLowPrime(targetPrime.shiftRight(1).subtract( targetPrime.shiftRight(2)));
			Integer [] idx3 = {spIdx-2, spIdx-1, spIdx};
			BigInteger [] sum3 = new BigInteger[3];
			boolean wasHigher = false;
			boolean wasLower = false;
			int compResult;
			do
			{
				for (int i = 0; i< idx3.length; i++)
				{
					sum3[i] = ps.getPrime(idx3[i]);
				}
				
				BigInteger totalSum = Arrays.asList(sum3).stream().reduce(BigInteger.ZERO, BigInteger::add);
	
				compResult = totalSum.compareTo(targetPrime);
				BigInteger totalDiff = targetPrime.subtract(totalSum).abs();
				
				if (compResult == 0)
				{
					System.out.println(String.format("equal[%b] Target prime[%d] primeidx[%d] idx1-3[%s] sum1-3[%s]  total[%s]", 
							targetPrime.equals(totalSum),
							targetPrime, 
							primeIdx, 
							Arrays.asList(idx3).stream().map(i -> Integer.toString(i)).collect(Collectors.joining(",")), 
							Arrays.asList(sum3).stream().map(BigInteger::toString).collect(Collectors.joining(",")),
							totalSum.toString())); 	
				}
				else if (compResult < 0) // total sum less than target; need to add to sum
				{
					wasLower = true;	
					System.out.println(String.format("equal[%b] Target prime[%d] primeidx[%d] idx1-3[%s] sum1-3[%s]  total[%s]", 
							targetPrime.equals(totalSum),
							targetPrime, 
							primeIdx, 
							Arrays.asList(idx3).stream().map(i -> Integer.toString(i)).collect(Collectors.joining(",")), 
							Arrays.asList(sum3).stream().map(BigInteger::toString).collect(Collectors.joining(",")),
							totalSum.toString()));
				
					// If difference is less than 0 then increasing top-item index will overshoot 
					BigInteger topItemDiffDiff = totalDiff.subtract(sum3[2]); 
					if (topItemDiffDiff.signum() > 0)
					{
						idx3[2]++;
						BigInteger midItemDiffDiff = topItemDiffDiff.subtract(sum3[1]);
						if (midItemDiffDiff.signum() > 0)
						{
							idx3[1]++;
							BigInteger lowItemDiffDiff = midItemDiffDiff.subtract(sum3[0]);
							if (lowItemDiffDiff.signum() > 0)
							{
								idx3[0]++;
							}
						}
					}
				}
				else // compResult > 0; totalsum greater than target; need to remove from sum
				{
					wasHigher = true;
				
					// If difference is higher than 0 then descreasing bottom-item index will undershoot 
						
					BigInteger bottomItemDiffDiff = totalDiff.subtract(sum3[0]); 
					if (bottomItemDiffDiff.signum() > 0)
					{
						idx3[0]--;
						BigInteger midItemDiffDiff = bottomItemDiffDiff.subtract(sum3[1]);
						if (midItemDiffDiff.signum() > 0)
						{
							idx3[1]--;
							BigInteger highItemDiffDiff = midItemDiffDiff.subtract(sum3[2]);
							if (highItemDiffDiff.signum() > 0)
							{
								idx3[2]--;
							}
						}
					}
				}
			} while(compResult != 0 && !(wasHigher == true && wasLower == true));
		}
		else
			System.out.println(String.format("Prime [%s] already has base-count of 3", targetPrime.toString()));
	};
		
	/**
	 *
	 * @param idx idx current index to reduce;
	 * @param reducer function implementing reduction algo
	 */
	private void treeReduction(int curPrimeIdx, BitSet bs, Consumer<BitSet> treeReducer)
	{	
		treeReducer.accept(bs);
	}	
	
	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	public void logTree(int maxReduce)
	{
		int curPrimeIdx = 15;
		while(true) 
		{ 
			PrimeRefIntfc curPrime;				
			try
			{
				curPrime = ps.getPrimeRef(curPrimeIdx);
				
				treeReduction(curPrimeIdx, curPrime.getPrimeBaseIdxs(), treeReducer.apply(curPrimeIdx, curPrime.getPrimeBaseIdxs()));
				curPrimeIdx--;
			}
			catch(Exception e)
			{
				break;
			}				
		}	
	}	
	
	private void populateData()
	{
		// Start setting up the actual graph/data generations
		PrimeNodeGenerator primeNodeGenerator = new PrimeNodeGenerator(ps, primeGraph);
		primeNodeGenerator.begin();
		
		while (primeNodeGenerator.nextEvents());		
	}
	
	public void viewDefault()
	{
        try
		{
			VisualizeGraph frame = new VisualizeGraph(this.graph);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setSize(400, 320);
			frame.setVisible(true);	
			frame.getRootPane().grabFocus();
			do
			{
				// will exit when window closes
			} while (System.in.read() != -1);
		}
		catch(Exception e)
		{
			log.severe("Exception:" + e);
		}	        
	}
}
