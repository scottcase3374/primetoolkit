package com.starcases.prime.graph.visualize.impl;

/**
 * Visualization example
 *
 * Circular layout/display of Prime / Prime-base relationships.
 */
import javax.swing.JFrame;
import java.awt.*;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.map.ImmutableMap;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.graph.visualize.api.VisualizationProviderIntfc;

import lombok.NonNull;
public class CircularLayoutPrimesGraph  extends JFrame implements VisualizationProviderIntfc
{
	private static final long serialVersionUID = 1L;

	private static final ImmutableCollection<String> ATTRIBUTES = Lists.immutable.of("VISUALIZATION", "CIRCULAR_LAYOUT");

	/**
	 * Dimension instance for default view size
	 */
	@NonNull
    private static final Dimension DEFAULT_SIZE = new Dimension(900,900);

	/**
	 * Constructor for the circular layout of default base info
	 * @param graph
	 */
    public CircularLayoutPrimesGraph()
    {
    	super();
    }

	@Override
	public int countAttributesMatch(final ImmutableCollection<String> attributes)
	{
		int ret = 0;
		if (ATTRIBUTES.containsAllIterable(attributes))
		{
			ret = ATTRIBUTES.size();
		}
		return ret;
	}

	@Override
	public JFrame create(@NonNull final Graph<PrimeRefIntfc,DefaultEdge> graph, final ImmutableMap<String, Object> attributes)
	{

        // create a visualization using JGraph, via an adapter
		final JGraphXAdapter<PrimeRefIntfc, DefaultEdge> jgxAdapter = new JGraphXAdapter<>(graph);

        final mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        super.getContentPane().add(component);

        // circle layout
        final var radius = 100;
        final var layout = new mxCircleLayout(jgxAdapter);
        layout.setX0(DEFAULT_SIZE.width / 2.0 - radius);
        layout.setY0(DEFAULT_SIZE.height / 2.0 - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);

        layout.execute(jgxAdapter.getDefaultParent());
		return this;
	}
}
