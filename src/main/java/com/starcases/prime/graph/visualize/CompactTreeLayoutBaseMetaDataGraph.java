package com.starcases.prime.graph.visualize;

import javax.swing.JFrame;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.starcases.prime.intfc.PrimeRefIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * visualization example
 *
 * Not doing much of anything right now..
 *
 */
public class CompactTreeLayoutBaseMetaDataGraph extends JFrame
{
	private static final long serialVersionUID = 1L;

	/**
	 * Sonar complains about wanting jgxAdapter to be either serializable or transient
	 */
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final JGraphXAdapter<PrimeRefIntfc, DefaultEdge> jgxAdapter;


	/**
	 * Constructor for tree layout of meta data info
	 * @param graph
	 */
    public CompactTreeLayoutBaseMetaDataGraph(@NonNull final Graph<PrimeRefIntfc,DefaultEdge> graph)
    {
    	super();
        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(graph);

        final var component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);

        final var layout = new mxCompactTreeLayout(jgxAdapter, false, false);
        layout.setEdgeRouting(true);
        layout.setLevelDistance(140);
        layout.setNodeDistance(140);
        layout.setResizeParent(true);
        layout.setGroupPadding(100);
        layout.setMoveTree(true);
        layout.setInvert(true);

        layout.execute(jgxAdapter.getDefaultParent());
    }
}
