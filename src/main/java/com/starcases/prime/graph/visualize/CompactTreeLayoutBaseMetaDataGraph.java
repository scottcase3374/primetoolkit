package com.starcases.prime.graph.visualize;

import javax.swing.JFrame;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.starcases.prime.intfc.PrimeRefIntfc;

/**
 * Display 
 * 	prime/max-base-size
 * 	prime/max dist to prev prime
 *  avg base size
 *  
 * @author scott
 *
 */
public class CompactTreeLayoutBaseMetaDataGraph extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private transient JGraphXAdapter<PrimeRefIntfc, DefaultEdge> jgxAdapter;

    
    public CompactTreeLayoutBaseMetaDataGraph(Graph<PrimeRefIntfc,DefaultEdge> graph)
    {
        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(graph);

        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
        
        mxCompactTreeLayout layout = new mxCompactTreeLayout(jgxAdapter, false, false);
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
