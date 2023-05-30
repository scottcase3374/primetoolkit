package com.starcases.prime.graph.visualize.impl;

import javax.swing.JFrame;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.map.ImmutableMap;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

import lombok.NonNull;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.graph.visualize.api.VisualizationProviderIntfc;

/**
 * Visualization example
 *
 * Uses a tree oriented layout/display to show the primes
 * 	and Prime-base relationships.
 * Inverts the tree to make some of the data more obvious - if
 * 	using large number of primes,
 * the graphs become so dense as to provide little use.
 *
 *
 */
class CompactTreeLayoutPrimesGraph extends JFrame implements VisualizationProviderIntfc
{
	private static final long serialVersionUID = 1L;

	private static final ImmutableCollection<String> ATTRIBUTES = Lists.immutable.of("VISUALIZATION", "COMPACT_TREE_LAYOUT");

	/**
	 * Constructor for compact tree layout of default base data
	 * @param graph
	 */
    public CompactTreeLayoutPrimesGraph()
    {
    	super();
    }

	@Override
	public JFrame create(@NonNull final Graph<PrimeRefIntfc,DefaultEdge> graph, final ImmutableMap<String, Object> attributes)
	{
	      // create a visualization using JGraph, via an adapter
		final JGraphXAdapter<PrimeRefIntfc, DefaultEdge> jgxAdapter = new JGraphXAdapter<>(graph);

        final var component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        super.getContentPane().add(component);

        final var layout = new mxCompactTreeLayout(jgxAdapter, false, false);
        layout.setEdgeRouting(true);
        layout.setLevelDistance(140);
        layout.setNodeDistance(140);
        layout.setResizeParent(true);
        layout.setGroupPadding(100);
        layout.setMoveTree(true);
        layout.setInvert(true);

        layout.execute(jgxAdapter.getDefaultParent());
		return this;
	}

	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
