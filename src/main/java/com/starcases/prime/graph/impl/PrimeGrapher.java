package com.starcases.prime.graph.impl;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import lombok.NonNull;

/**
 * Provide support for the graph oriented processing - base class.
 *
 */
public abstract class PrimeGrapher 
{
	@NonNull
	protected static final Comparator<PrimeRefIntfc> nodeComparator = (PrimeRefIntfc o1, PrimeRefIntfc o2) -> o1.getPrime().compareTo(o2.getPrime());

	@NonNull
	protected PrimeSourceIntfc ps;
	
	@NonNull
	protected final GraphBuilder<PrimeRefIntfc, DefaultEdge, DefaultDirectedGraph<PrimeRefIntfc, DefaultEdge>> primeGraphBuilder = new GraphBuilder<>(new DefaultDirectedGraph<>(DefaultEdge.class));
	
	@NonNull
	protected Graph<PrimeRefIntfc,DefaultEdge> graph;
	
	@NonNull
	protected Logger log;
	
	@NonNull
	protected BaseTypes baseType;
	
	/**
	 * 
	 * @param ps
	 * @param log
	 * @param graphs
	 */
	protected PrimeGrapher(@NonNull PrimeSourceIntfc ps, @NonNull Logger log, @NonNull BaseTypes baseType)
	{
		this(ps, log, baseType, Collections.emptyList());
	}	
	
	/**
	 * 
	 * @param ps
	 * @param log
	 * @param graphs
	 */
	protected PrimeGrapher(@NonNull PrimeSourceIntfc ps, @NonNull Logger log, @NonNull BaseTypes baseType, @NonNull List<GraphListener<PrimeRefIntfc, DefaultEdge>> graphs)
	{
		this.log = log;
		this.ps = ps;
		this.baseType = baseType;
		this.ps.init();
		
		var lgraph = new DefaultListenableGraph<PrimeRefIntfc, DefaultEdge>(primeGraphBuilder.build(), true);
		graphs.stream().forEach(lgraph::addGraphListener);
		this.graph = lgraph;
		
		this.populateData();
	}
	
	BigInteger getTotalSum(@NonNull BigInteger [] sum3, @NonNull Integer [] indexes)
	{
		for (var i = 0; i< sum3.length; i++)
		{
			final var x = i;
			ps.getPrime(indexes[i]).ifPresent(p -> sum3[x] = p);
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
