package com.starcases.prime.graph.impl;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.graph.DefaultListenableGraph;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;

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
	protected final PrimeSourceIntfc ps;

	@NonNull
	protected final GraphBuilder<PrimeRefIntfc, DefaultEdge, DefaultDirectedGraph<PrimeRefIntfc, DefaultEdge>> primeGraphBuilder = new GraphBuilder<>(new DefaultDirectedGraph<>(DefaultEdge.class));

	@NonNull
	protected final Graph<PrimeRefIntfc,DefaultEdge> graph;

	@NonNull
	protected final BaseTypes baseType;

	/**
	 * General constructor
	 *
	 */
	protected PrimeGrapher(@NonNull PrimeSourceIntfc ps, @NonNull BaseTypes baseType)
	{
		this(ps, baseType, Collections.emptyList());
	}

	/**
	 * Provide support for visual output related to graphs
	 */
	protected PrimeGrapher(@NonNull PrimeSourceIntfc ps, @NonNull BaseTypes baseType, @NonNull List<GraphListener<PrimeRefIntfc, DefaultEdge>> graphs)
	{
		this.ps = ps;
		this.baseType = baseType;
		this.ps.init();

		final var lgraph = new DefaultListenableGraph<PrimeRefIntfc, DefaultEdge>(primeGraphBuilder.build(), true);
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

	/**
	 * Perform the data population
	 */
	void populateData()
	{
		// Start setting up the actual graph/data generations
		final var primeNodeGenerator = new PrimeNodeGenerator(ps, graph, baseType);
		primeNodeGenerator.begin();

		while (primeNodeGenerator.nextEvents());
	}
}
