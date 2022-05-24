package com.starcases.prime.graph.visualize;

import javax.swing.JFrame;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * Visualization example
 *
 * Not doing much of anything right now with this.
 *
 */
public class ParallelEdgeLayoutGraph extends JFrame
{
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final transient JGraphXAdapter<String, DefaultEdge> jgxAdapter;

	/**
	 * Constructor for parallel edge layout of data
	 * @param graph
	 */
    public ParallelEdgeLayoutGraph(@NonNull final Graph<String,DefaultEdge> graph)
    {
    	super();

        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(graph);

        final var component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);

        //parallel edge layout - not useful at moment
        final var layout = new mxParallelEdgeLayout(jgxAdapter, 120);

        layout.execute(jgxAdapter.getDefaultParent());
    }
}
