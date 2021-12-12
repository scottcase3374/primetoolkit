package com.starcases.prime.graph.impl;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.graph.DefaultListenableGraph;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.BaseTypes;

/**
 * Provide support for the graph oriented processing - base class.
 *
 */
public abstract class PrimeGrapher 
{
	protected static Comparator<PrimeRefIntfc> nodeComparator = (PrimeRefIntfc o1, PrimeRefIntfc o2) -> o1.getPrime().compareTo(o2.getPrime());

	protected PrimeSourceIntfc ps;
	
	protected GraphBuilder<PrimeRefIntfc, DefaultEdge, DefaultDirectedGraph<PrimeRefIntfc, DefaultEdge>> primeGraphBuilder = new GraphBuilder<>(new DefaultDirectedGraph<>(DefaultEdge.class));
	protected Graph<PrimeRefIntfc,DefaultEdge> graph;
	
	protected Logger log;
	protected BaseTypes baseType;
	
	/**
	 * 
	 * @param ps
	 * @param log
	 * @param graphs
	 */
	protected PrimeGrapher(PrimeSourceIntfc ps, Logger log, BaseTypes baseType, GraphListener...graphs)
	{
		this.log = log;
		this.ps = ps;
		this.baseType = baseType;
		this.ps.init();
		
		var lgraph = new DefaultListenableGraph(primeGraphBuilder.build(), true);
		Arrays.asList(graphs).stream().forEach(lgraph::addGraphListener);
		this.graph = lgraph;
		
		this.populateData();
	}
	
	BigInteger getTotalSum(BigInteger [] sum3, Integer [] indexes)
	{
		for (var i = 0; i< sum3.length; i++)
		{
			sum3[i] = ps.getPrime(indexes[i]);
		}		
		return Arrays.asList(sum3).stream().reduce(BigInteger.ZERO, BigInteger::add);
	}
	
	void populateData()
	{
		// Start setting up the actual graph/data generations
		var primeNodeGenerator = new PrimeNodeGenerator(ps, graph, baseType);
		primeNodeGenerator.begin();
		
		while (primeNodeGenerator.nextEvents());		
	}
}
