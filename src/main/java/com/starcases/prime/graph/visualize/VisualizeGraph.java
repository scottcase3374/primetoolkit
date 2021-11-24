package com.starcases.prime.graph.visualize;

import javax.swing.JFrame;
import java.awt.*;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;

public class VisualizeGraph extends JFrame
{
	private static final long serialVersionUID = 1L;
	private transient JGraphXAdapter<String, DefaultEdge> jgxAdapter;
    private final transient java.awt.Dimension DEFAULT_SIZE = new Dimension(900,900);
    
    public VisualizeGraph(Graph<String,DefaultEdge> graph)
    {
        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(graph);

        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
      
        // parallel edge layout - not useful at moment
        // mxParallelEdgeLayout layout = new mxParallelEdgeLayout(jgxAdapter, 120);
        
        /*
        mxCompactTreeLayout layout = new mxCompactTreeLayout(jgxAdapter, false, false);
        layout.setEdgeRouting(true);
        layout.setLevelDistance(140);
        layout.setNodeDistance(140);
        layout.setResizeParent(true);
        layout.setGroupPadding(100);
        layout.setMoveTree(true);
        layout.setInvert(true);
        */
        
        // circle layout
        int radius = 100;
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);        
        layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);
		
        
        layout.execute(jgxAdapter.getDefaultParent());
    }
}
