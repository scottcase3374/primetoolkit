package com.starcases.prime.graph.visualize.impl;

import javax.swing.JFrame;

//import org.eclipse.collections.api.collection.ImmutableCollection;
//import org.eclipse.collections.api.factory.Lists;
//import org.eclipse.collections.api.map.ImmutableMap;
//import org.jgrapht.Graph;
//import org.jgrapht.ext.JGraphXAdapter;
//import org.jgrapht.graph.DefaultEdge;
//import com.mxgraph.layout.mxParallelEdgeLayout;
//import com.mxgraph.swing.mxGraphComponent;
//import com.starcases.prime.core.api.PrimeRefIntfc;
//import com.starcases.prime.graph.visualize.api.VisualizationProviderIntfc;
//
//import lombok.NonNull;

/**
 * Visualization example
 *
 * Not doing much of anything right now with this.
 *
 */
class ParallelEdgeLayoutGraph extends JFrame //implements VisualizationProviderIntfc
{
	private static final long serialVersionUID = 1L;

//	private static final ImmutableCollection<String> ATTRIBUTES = Lists.immutable.of("VISUALIZATION", "PARALLEL_EDGE_LAYOUT");

	/**
	 * Constructor for parallel edge layout of data
	 * @param graph
	 */
    public ParallelEdgeLayoutGraph()
    {
    	super();
    }
	/*
	 *
	 * @Override public JFrame create(@NonNull final
	 * Graph<PrimeRefIntfc,DefaultEdge> graph, final ImmutableMap<String, Object>
	 * attributes) { // create a visualization using JGraph, via an adapter final
	 * JGraphXAdapter<String, DefaultEdge> jgxAdapter = new JGraphXAdapter<>(graph);
	 * //final JGraphXAdapter<PrimeRefIntfc, DefaultEdge> jgxAdapter = new
	 * JGraphXAdapter<>(graph); final var component = new
	 * mxGraphComponent(jgxAdapter); component.setConnectable(false);
	 * component.getGraph().setAllowDanglingEdges(false);
	 * super.getContentPane().add(component);
	 *
	 * //parallel edge layout - not useful at moment final var layout = new
	 * mxParallelEdgeLayout(jgxAdapter, 120);
	 *
	 * layout.execute(jgxAdapter.getDefaultParent()); return this; }
	 */
}
