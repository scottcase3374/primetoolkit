package com.starcases.prime.graph.visualize.impl;

//import javax.swing.WindowConstants;

import java.io.IOException;
import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;

//import org.eclipse.collections.api.collection.ImmutableCollection;
//import org.eclipse.collections.api.factory.Lists;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultEdge;

import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.graph.impl.PrimeGrapherBase;
import com.starcases.prime.kern.api.BaseTypesIntfc;

import lombok.NonNull;

/**
 * Visualization
 *
 * Default graphing entry point - provides circular
 * layout and compact tree layout based on
 * same underlying data.
 *
 * supports inclusion of other visuals/displays (such as MetaData Table) via GraphListener.
 */
public class ViewDefault extends PrimeGrapherBase
{
//	private static final ImmutableCollection<String> PRIMARY_ATTRIBUTES = Lists.immutable.of("VISUALIZATION");
//	private static final ImmutableCollection<String> VISUALIZATION_NAMES = Lists.immutable.of("CIRCULAR_LAYOUT", "COMPACT_TREE_LAYOUT");

	/**
	 * Default logger
	 */
//	private static final Logger LOG = Logger.getLogger(ViewDefault.class.getName());

	/**
	 * Constructor for default view setup
	 * @param primeSrc
	 * @param baseType
	 * @param graphs
	 */
	public ViewDefault(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final BaseTypesIntfc baseType, @NonNull final List<GraphListener<PrimeRefIntfc, DefaultEdge>> graphs)
	{
		super(primeSrc, baseType, graphs);
	}

	/**
	 * Display the view
	 * @throws IOException
	 */
	public void viewDefault() throws IOException
	{
//		if (LOG.isLoggable(Level.INFO))
//		{
//			LOG.fine("Display default View");
//		}
//
//		final SvcLoader<VisualizationProviderIntfc, Class<VisualizationProviderIntfc>> visualizationProvider = new SvcLoader< >(VisualizationProviderIntfc.class);
//		visualizationProvider
//			.providers(PRIMARY_ATTRIBUTES)
//			.collectIf(f -> f.countAttributesMatch(VISUALIZATION_NAMES) > 0, p -> p)
//			.forEach(p ->
//
//							{
//								final var jf = p.create(this.graph, null);
//								jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//								jf.setSize(400, 320);
//								jf.setVisible(true);
//								jf.getRootPane().grabFocus();
//							});
//
//		// cause exit when window closes
//		while (System.in.read() != -1);
	}
}
