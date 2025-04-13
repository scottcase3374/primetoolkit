package com.starcases.prime.graph.visualize.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.WindowConstants;

import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.graph.impl.PrimeGrapherBase;
import com.starcases.prime.graph.visualize.api.VisualizationProviderIntfc;
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
	private final ImmutableList<VisualizationProviderIntfc> providers;

	/**
	 * Default logger
	 */
	private static final Logger LOG = Logger.getLogger(ViewDefault.class.getName());

	/**
	 * Constructor for default view setup
	 * @param primeSrc
	 * @param baseType
	 * @param graphs
	 */
	public ViewDefault(	@NonNull final PrimeSourceIntfc primeSrc,
						@NonNull final BaseTypesIntfc baseType,
						ImmutableList<VisualizationProviderIntfc> providers,
						final int maxGraphIndex)
	{
		super(primeSrc, baseType, maxGraphIndex);
		this.providers = providers;
	}

	/**
	 * Display the view
	 * @throws IOException
	 */
	public void viewDefault() throws IOException
	{
		this.populateData();
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("*** Display default View");
		}

		providers
			.tap(p -> LOG.info(String.format("view default: vis prov attrs: %s ", p.getProviderAttributes().makeString())))
			.forEach(p ->
							{
								final var jf = p.create(this.graph, null);
								jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
								jf.setSize(400, 320);
								jf.setVisible(true);
								jf.getRootPane().grabFocus();
							});

		// cause exit when window closes
		while (System.in.read() != -1);
	}
}
