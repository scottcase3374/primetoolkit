package com.starcases.prime.graph.visualize.impl;

import javax.swing.JFrame;

/**
 * Visualization example
 *
 * Not doing much of anything right now with this.
 *
 */
public class ParallelEdgeLayoutGraph extends JFrame //implements VisualizationProviderIntfc
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
	 * @Override public int countAttributesMatch(final ImmutableCollection<String>
	 * attributes) { int ret = 0; if (ATTRIBUTES.containsAllIterable(attributes)) {
	 * ret = ATTRIBUTES.size(); } return ret; }
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
