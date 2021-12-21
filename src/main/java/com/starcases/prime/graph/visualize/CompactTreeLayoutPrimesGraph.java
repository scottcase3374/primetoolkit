package com.starcases.prime.graph.visualize;

import javax.swing.JFrame;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;

/**
 * Uses a tree oriented layout/display to show the primes and prime-base relationships.
 * Inverts the tree to make some of the data more obvious - if using large number of primes,
 * the graphs become so dense as to provide little use.
 *  
 *
 */
public class CompactTreeLayoutPrimesGraph extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private transient JGraphXAdapter<PrimeRefIntfc, DefaultEdge> jgxAdapter;
	
    public CompactTreeLayoutPrimesGraph(Graph<PrimeRefIntfc,DefaultEdge> graph)
    {
        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(graph);

        var component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
        
        var layout = new mxCompactTreeLayout(jgxAdapter, false, false);
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