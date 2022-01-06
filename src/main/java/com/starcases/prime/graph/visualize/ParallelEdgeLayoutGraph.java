package com.starcases.prime.graph.visualize;

import javax.swing.JFrame;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;

import lombok.NonNull;

/**
 * 
 * Not doing much of anything right now with this.
 *
 */
public class ParallelEdgeLayoutGraph extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	@NonNull
	private transient JGraphXAdapter<String, DefaultEdge> jgxAdapter;
  
    public ParallelEdgeLayoutGraph(@NonNull Graph<String,DefaultEdge> graph)
    {
        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(graph);

        var component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
      
        //parallel edge layout - not useful at moment
        var layout = new mxParallelEdgeLayout(jgxAdapter, 120);
        
        layout.execute(jgxAdapter.getDefaultParent());
    }
}
