package com.starcases.prime.graph.visualize;

import javax.swing.JFrame;
import java.awt.*;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.starcases.prime.intfc.PrimeRefIntfc;
public class CircularLayoutPrimesGraph extends JFrame
{
	private static final long serialVersionUID = 1L;
	private transient JGraphXAdapter<PrimeRefIntfc, DefaultEdge> jgxAdapter;
    private static final Dimension DEFAULT_SIZE = new Dimension(900,900);
    
    public CircularLayoutPrimesGraph(Graph<PrimeRefIntfc,DefaultEdge> graph)
    {
        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(graph);

        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
      
        // circle layout
        var radius = 100;
        var layout = new mxCircleLayout(jgxAdapter);        
        layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);
		
        
        layout.execute(jgxAdapter.getDefaultParent());
    }
}
