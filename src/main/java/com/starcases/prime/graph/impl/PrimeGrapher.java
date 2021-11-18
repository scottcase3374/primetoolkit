package com.starcases.prime.graph.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
						graph.outgoingEdgesOf(n).stream().map(e -> graph.getEdgeTarget(e)).collect(Collectors.joining(","))
			) ) );		
	}
	
	public void logNodeStructure()
	{
		try
		{
			for (int i = 0; i < ps.getMaxIdx(); i++)
			{ 
				PrimeRefIntfc ref = ps.getPrimeRef(i);
				System.out.println(String.format("Prime %d bases %s  <dist[%d], nextPrime[%d]>", 
						ref.getPrime(), 
						ref.getIdxPrimes(), 
						ps.getDistToNextPrime(ref.getPrimeRefIdx()),
						ps.getPrime(ref.getPrimeRefIdx()+1)));
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

	private BigInteger getTotalSum(BigInteger [] sum3, Integer [] indexes)
	{
		for (int i = 0; i< sum3.length; i++)
		{
			sum3[i] = ps.getPrime(indexes[i]);
		}		
		return Arrays.asList(sum3).stream().reduce(BigInteger.ZERO, BigInteger::add);
	}
	
	private String descOp(	String when,
							String initialPrimes,
							String initialIndexes,							
							BigInteger initialSum,
							Integer [] idx3Offsets,
							Integer [] tmpCalcedIdxs,
							BigInteger [] tmpPrimes,
							BigInteger tmpTotalSum,
							BigInteger tmpDiff,  
							BigInteger requiredDiff, 
							BigInteger targetPrime)
	{	
		return String.format("%5s init-idxs[%7s] init-prim[%s] init-sum[%3d] Idx-off-chgs[%8s] ## tmp-idxs[%8s] tmp-prim[%8s] tmp-ttl-sum[%d] tmp-ttl-diff[%d] *** target-diff[%d] target-prime[%d]",
				when, 																											// when
				initialIndexes,																										// init idxs
				initialPrimes,																										// init set of primes to sum
				initialSum,																											// init totalSum
				
				Arrays.asList(idx3Offsets).stream().map(i -> Integer.toString(i)).collect(Collectors.joining(",")), 				// idx3offsets
				Arrays.asList(tmpCalcedIdxs).stream().map(i -> Integer.toString(i)).collect(Collectors.joining(",")), 			// tmp3indexes				
				Arrays.asList(tmpPrimes).stream().map(BigInteger::toString).collect(Collectors.joining(",")), 				// tmpPrimes	
				tmpTotalSum,
				tmpDiff,																											// sum-offset
				requiredDiff, 																									// required diff
				targetPrime);																									// target prime
	}
	

	/**
	 * 
	 * @param targetDiff
	 * @param idx3Offsets
	 * @param sum3
	 * @param sum3Idxs
	 * @param targetPrime
	 * @param func
	 * @return 
	 */
	private Optional<BigInteger> handleIncrement(BigInteger targetDiff, 
												Integer [] idx3Offsets,  
												BigInteger [] sum3, 
												Integer [] sum3Idxs, 
												BigInteger targetPrime, 
												BiFunction<BigInteger, BigInteger, Boolean> func)
	{	
		Integer [] tmpCalcedIdxs = { sum3Idxs[0] + idx3Offsets[0], sum3Idxs[1] + idx3Offsets[1],sum3Idxs[2] + idx3Offsets[2]};
		
		if (Arrays.asList(tmpCalcedIdxs).stream().distinct().count() != 3)
		{
			System.out.println("idx overlap: " + Arrays.asList(tmpCalcedIdxs).stream().map(i -> Integer.toString(i)).collect(Collectors.joining(",", "[","]")));
			return Optional.empty();
		}
		
		BigInteger [] tmpPrimes = {
				ps.getPrime(tmpCalcedIdxs[0]),
				ps.getPrime(tmpCalcedIdxs[1]),
				ps.getPrime(tmpCalcedIdxs[2])
		};
		
		BigInteger [] tmpDiffsNextPrime = 
			{ 
				tmpPrimes[0].subtract(ps.getPrime(sum3Idxs[0])), 
				tmpPrimes[1].subtract(ps.getPrime(sum3Idxs[1])),
				tmpPrimes[2].subtract(ps.getPrime(sum3Idxs[2]))
			};
		
		BigInteger tmpTotalDiff = Arrays.asList(tmpDiffsNextPrime).stream().reduce(BigInteger.ZERO, BigInteger::add);
		BigInteger tmpTotalSum = Arrays.asList(tmpPrimes).stream().reduce(BigInteger.ZERO, BigInteger::add);
		
		BigInteger initialSum = Arrays.asList(sum3).stream().reduce(BigInteger.ZERO, BigInteger::add);
		
		String initSum3Idxs = Arrays.asList(sum3Idxs).stream().map(i -> Integer.toString(i)).collect(Collectors.joining(","));
		String initPrimes = Arrays.asList(sum3).stream().map(BigInteger::toString).collect(Collectors.joining(","));
		System.out.println(descOp("Start", initPrimes, initSum3Idxs, initialSum, idx3Offsets, tmpCalcedIdxs, tmpPrimes, tmpTotalSum, tmpTotalDiff, targetDiff, targetPrime));
		
		if (func.apply(tmpTotalDiff, targetDiff))
		{
			for (int i=0; i< sum3Idxs.length; i++)
			{
				sum3Idxs[i] = tmpCalcedIdxs[i];
			}
			BigInteger totalSum = getTotalSum(sum3, sum3Idxs);
			System.out.println(descOp("Post",initPrimes, initSum3Idxs, initialSum, idx3Offsets, tmpCalcedIdxs, tmpPrimes, tmpTotalSum, tmpTotalDiff, targetDiff, targetPrime));
			return Optional.of(totalSum);
		}
		return Optional.empty();
	}
	
	Integer [][] buildpermutations(int bits, int [] vals)
	{
		
		int rows = (int)Math.pow(bits, vals.length);
		Integer [][] result = new Integer[bits][];
		for (int i=0; i < rows; i++)
		{
			result[i] = new Integer[bits];
		}
		return result;
	}
	/*
	 *  given prime-idx, bitset
	 *  
	 *  find new bitset where bitset.size()-1 is reduced to the sum of
	 *  3 pre-existing primes. For example.
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
	 * 21 73 -> 113,29,31
	 * 22 79 -> 19,29,31
	 * 23 83 -> 23,29,31 *
	 * 24 89 -> 19,29,41
	 * 25 97 -> 13,41,43; 19,37,41
	 * 26 101-> 23,37,41
	 * 27 103->
	 * 28 107-> 
	 * 29 109-> 31,37,41 *	
	 */
	final BiFunction<Integer, BitSet, Consumer<BitSet>> log3BaseReducer = (primeIdx, bs)-> idx -> 
	{
		Integer targetPrimeIdx = primeIdx;
		BigInteger targetPrime = ps.getPrime(targetPrimeIdx);
			
		// Programmatic guess at starting point indexes versus brute force entire range
		BigInteger tmpVal = targetPrime.shiftRight(1).subtract( targetPrime.shiftRight(2));
		Integer spIdx = ps.getNextLowPrime(tmpVal);
		
		// values for each index which are used in various permutations to determine changes needed to reach targetPrime
		Integer [] idx3 = {spIdx, spIdx+1, spIdx+2};
		BigInteger [] sum3 = new BigInteger[3];
		BigInteger totalSum = getTotalSum(sum3, idx3); // initialize the primes at at idx and total it
	
		// allowed adjustments [unless result in idx overlap]
		Integer [][] posOffsets = {
				{1,1,1},  // incAllIdx
				{0,1,1},   // incTop2Idx
				{0,0,1},
				{-1, 1, 1},
				{-1, 0, 1}
		};
		
		Integer [][] negOffsets = {
				{-1,-1,-1},	
				 {-1,-1, 0},
				 {-1, 0, 0} 
		};
		
		/*final Integer [] incAllIdx   	= {1,1,1};
		final Integer [] incTop2Idx 	= {0,1,1};
		final Integer [] incTopIdx 		= {0,0,1};
		final Integer [] incBotIdx      = {1, 0, 0};

		final Integer [] incTop2IdxDecBotIdx = {-1,  1, 1};
		final Integer [] incTopIdxDec2BotIdx = {-1, -1, 1};
		final Integer [] incTopIdxDecBotIdx = {-1,   0, 1};
		
		final Integer [] decAllIdx		= {-1,-1,-1};
		final Integer [] decBot2Idx 	= {-1,-1, 0};
		final Integer [] decBotIdx 		= {-1, 0, 0}; */
	
		do
		{
			final BigInteger requiredDiff = targetPrime.subtract(totalSum); 
			if (requiredDiff.signum() == 0)
				break;
			
			Optional<BigInteger> newSum =  Arrays.asList(posOffsets)
					.stream()
					.map(offset ->	handleIncrement(requiredDiff, offset ,  sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0))
					.findAny()
					.or(handleIncrement(requiredDiff, incAllIdx,  sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0));
		
			///	handleIncrement(requiredDiff, ,  sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0)	
			/*
				Optional<BigInteger> newSum =
							  handleIncrement(requiredDiff, incAllIdx,  sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0)								 	  
					.or(() -> handleIncrement(requiredDiff, incTop2Idx, sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0))
					.or(() -> handleIncrement(requiredDiff, incTopIdx,  sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0))
					.or(() -> handleIncrement(requiredDiff, incBotIdx, sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0))
					
					.or(() -> handleIncrement(requiredDiff, incTop2IdxDecBotIdx, sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0))	
					.or(() -> handleIncrement(requiredDiff, incTopIdxDec2BotIdx, sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0))
					.or(() -> handleIncrement(requiredDiff, incTopIdxDecBotIdx, sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0))

					.or(() -> handleIncrement(requiredDiff, decAllIdx,  sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0))
					.or(() -> handleIncrement(requiredDiff, decBot2Idx, sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0))
					.or(() -> handleIncrement(requiredDiff, decBotIdx,  sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) == 0))
					
					
					.or(() -> handleIncrement(requiredDiff, incAllIdx,  sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) < 0))
					.or(() -> handleIncrement(requiredDiff, incTop2Idx, sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) < 0))

					.or(() -> handleIncrement(requiredDiff, decAllIdx, 	sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) > 0))
					.or(() -> handleIncrement(requiredDiff, decBotIdx, 	sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) > 0))
				
					.or(() -> handleIncrement(requiredDiff, decBot2Idx,sum3, idx3, targetPrime, (a, b) -> a.compareTo(b) > 0))
				;
	*/
			if (newSum.isPresent())
			{
				totalSum = newSum.get();				
			}	
		} 
		while(true);	
	
		BitSet bNew = new BitSet();
		Arrays.asList(idx3).stream().forEach(bNew::set);
		ps.getPrimeRef(primeIdx).addPrimeBase(bNew);					
		
		System.out.println(String.format("Added base to Prime[%d] Idx[%d]", targetPrime, targetPrimeIdx));
	};
	
	/**
	 *
	 * @param idx idx current index to reduce;
	 * @param reducer function implementing reduction algo
	 */
	private void log3BaseReduction(BitSet bs, Consumer<BitSet> log3BaseReducer)
	{	
		log3BaseReducer.accept(bs);
	}	
	
	/**
	 * top-level function; iterate over entire dataset to reduce every item
	 * @param maxReduce
	 */
	public void log3Base(int activeBaseId)
	{
		final int minPrimeIdx = ps.getPrimeIdx(BigInteger.valueOf(11L));
		for (int curPrimeIdx = 0; curPrimeIdx < minPrimeIdx; curPrimeIdx++)
		{
			BitSet bNew = new BitSet();
			bNew.set(0);
			ps.getPrimeRef(curPrimeIdx).addPrimeBase(bNew);
		}
		
		for(int curPrimeIdx=minPrimeIdx; curPrimeIdx < ps.getMaxIdx(); curPrimeIdx++) 
		{ 
			PrimeRefIntfc curPrime = ps.getPrimeRef(curPrimeIdx);				
			try
			{
				log3BaseReduction(curPrime.getPrimeBaseIdxs(), log3BaseReducer.apply(curPrimeIdx, curPrime.getPrimeBaseIdxs()));
			}
			catch(Exception e)
			{
				log.severe(String.format("log3Base generation => prime-idx [%d] prime [%d] error: %s", curPrimeIdx, curPrime.getPrime(), e.toString()));
				break;
			}				
		}
		ps.setActiveBaseId(activeBaseId);
		for (int i = minPrimeIdx; i < ps.getMaxIdx(); i++)
		{ 				
			PrimeRefIntfc pr = ps.getPrimeRef(i);
			try 
			{
				System.out.println(String.format("Prime [%d] idx[%d] bases %s base-indexes %s",
						pr.getPrime(), 
						i,
						pr.getIdxPrimes(),
						pr.getIndexes()));
			}
			catch(Exception e)
			{
				System.out.println(String.format("Can't show bases for: %d", pr.getPrime()));
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
