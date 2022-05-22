package com.starcases.prime.graph.visualize;

/**
 * Visualization example
 *
 * Circular layout/display of Prime / Prime-base relationships.
 */
import javax.swing.JFrame;
import java.awt.*;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.starcases.prime.intfc.PrimeRefIntfc;

import lombok.NonNull;
public class CircularLayoutPrimesGraph extends JFrame
{
	private static final long serialVersionUID = 1L;

	// Sonar complains about wanting jgxAdapter to be either serializable or transient
	@NonNull
	private final transient JGraphXAdapter<PrimeRefIntfc, DefaultEdge> jgxAdapter;

	@NonNull
    private static final Dimension DEFAULT_SIZE = new Dimension(900,900);

	@SuppressWarnings("PMD.AvoidFinalLocalVariable")
    public CircularLayoutPrimesGraph(@NonNull final Graph<PrimeRefIntfc,DefaultEdge> graph)
    {
    	super();

        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(graph);

        final mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);

        // circle layout
        final var radius = 100;
        final var layout = new mxCircleLayout(jgxAdapter);
        layout.setX0(DEFAULT_SIZE.width / 2.0 - radius);
        layout.setY0(DEFAULT_SIZE.height / 2.0 - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);


        layout.execute(jgxAdapter.getDefaultParent());
    }
}
