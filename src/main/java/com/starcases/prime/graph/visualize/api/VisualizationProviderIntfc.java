package com.starcases.prime.graph.visualize.api;

import javax.swing.JFrame;

import org.eclipse.collections.api.map.ImmutableMap;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

/**
 * Interface for use with service loader to
 * load Visualization based services.
 * @author scott
 *
 */
public interface VisualizationProviderIntfc extends SvcProviderBaseIntfc
{
	JFrame create(@NonNull final Graph<PrimeRefIntfc,DefaultEdge> graph, final ImmutableMap<String, Object> attributes);
}
