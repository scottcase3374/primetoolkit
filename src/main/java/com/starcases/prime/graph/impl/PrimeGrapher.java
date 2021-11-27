package com.starcases.prime.graph.impl;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphBuilder;
import com.starcases.prime.intfc.PrimeSourceIntfc;

// 
// start 1
//       2
//       3 <-  2 + 1
//       5 <-  3 + 2
//       7 <-  5 + 2
//       11 <- 7+3+1; 5+3+2+1
public abstract class PrimeGrapher 
{
	protected static Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));

	protected PrimeSourceIntfc ps;
	
	protected GraphBuilder<String, DefaultEdge, DefaultDirectedGraph<String, DefaultEdge>> primeGraph = new GraphBuilder<>(new DefaultDirectedGraph<>(DefaultEdge.class));
	protected Graph<String,DefaultEdge> graph;
	
	protected Logger log;
	
	protected PrimeGrapher(PrimeSourceIntfc ps, Logger log)
	{
		this.log = log;
		this.ps = ps;
		this.ps.init();
		this.populateData();
		this.graph = primeGraph.build();
	}
	
	BigInteger getTotalSum(BigInteger [] sum3, Integer [] indexes)
	{
		for (int i = 0; i< sum3.length; i++)
		{
			sum3[i] = ps.getPrime(indexes[i]);
		}		
		return Arrays.asList(sum3).stream().reduce(BigInteger.ZERO, BigInteger::add);
	}
	
	void populateData()
	{
		// Start setting up the actual graph/data generations
		PrimeNodeGenerator primeNodeGenerator = new PrimeNodeGenerator(ps, primeGraph);
		primeNodeGenerator.begin();
		
		while (primeNodeGenerator.nextEvents());		
	}
}
