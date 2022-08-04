package com.starcases.prime.graph.impl;

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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * Provide support for the graph oriented processing - base class.
 *
 */
public class PrimeGrapherBase
{
	/**
	 * A comparator for node objects.
	 */
	@NonNull
	protected static final Comparator<PrimeRefIntfc> NODE_COMPARATOR = (PrimeRefIntfc o1, PrimeRefIntfc o2) -> (int)(o1.getPrime() - o2.getPrime());

	/**
	 * Access prime/primeref lookup functionality
	 */
	@Getter(AccessLevel.PROTECTED)
	@NonNull
	protected final PrimeSourceIntfc primeSrc;

	/**
	 * High-level Graph construction configuration - builder instance
	 * produces the actual graph instance.
	 */
	@Getter
	@NonNull
	protected final GraphBuilder<PrimeRefIntfc, DefaultEdge, DefaultDirectedGraph<PrimeRefIntfc, DefaultEdge>> primeGraphBuilder = new GraphBuilder<>(new DefaultDirectedGraph<>(DefaultEdge.class));

	/**
	 * Graph produced with PrimeRefs and DefaultEdge types.
	 */
	@Getter(AccessLevel.PROTECTED)
	@NonNull
	protected final Graph<PrimeRefIntfc,DefaultEdge> graph;

	/**
	 * Base type to source data from.
	 */
	@Getter(AccessLevel.PROTECTED)
	@NonNull
	protected final BaseTypes baseType;

	/**
	 * General constructor
	 *
	 */
	protected PrimeGrapherBase(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final BaseTypes baseType)
	{
		this(primeSrc, baseType, Collections.emptyList());
	}

	/**
	 * Provide support for visual output related to graphs
	 */
	protected PrimeGrapherBase(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final BaseTypes baseType, @NonNull final List<GraphListener<PrimeRefIntfc, DefaultEdge>> graphs)
	{
		this.primeSrc = primeSrc;
		this.baseType = baseType;
		this.primeSrc.init();

		final var lgraph = new DefaultListenableGraph<PrimeRefIntfc, DefaultEdge>(primeGraphBuilder.build(), true);
		graphs.stream().forEach(lgraph::addGraphListener);
		this.graph = lgraph;
	}

	/**
	 * Helper method for getting sum of valid items.
	 *
	 * @param sum3
	 * @param indexes
	 * @return
	 */
	protected long getTotalSum(@NonNull final long [] sum3, @NonNull final Integer [] indexes)
	{
		for (var index = 0; index< sum3.length; index++)
		{
			final var tmpIndex = index;
			primeSrc.getPrimeForIdx(indexes[tmpIndex]).ifPresent(p -> sum3[tmpIndex] = p);
		}
		return Arrays.stream(sum3).reduce(0L, (a,b) -> a+b);
	}

	/**
	 * Perform the data population
	 */
	public void populateData()
	{
		// Start setting up the actual graph/data generations
		final var primeNodeGenerator = new PrimeNodeGenerator(primeSrc, graph, baseType);
		primeNodeGenerator.begin();

		while(primeNodeGenerator.nextEvents())
		{
			 // iterates through the events which are processed elsewhere.
		}
	}
}
