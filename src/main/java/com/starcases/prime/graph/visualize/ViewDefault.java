package com.starcases.prime.graph.visualize;

import javax.swing.WindowConstants;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultEdge;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.graph.impl.AbstractPrimeGrapher;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;

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
@SuppressWarnings("PMD.CommentSize")
public class ViewDefault extends AbstractPrimeGrapher
{
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
	public ViewDefault(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final BaseTypes baseType, @NonNull final List<GraphListener<PrimeRefIntfc, DefaultEdge>> graphs)
	{
		super(primeSrc, baseType, graphs);
	}

	/**
	 * Display the view
	 * @throws IOException
	 */
	@SuppressWarnings({"PMD.LawOfDemeter"})
	public void viewDefault() throws IOException
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("Display default View");
		}
		final var defaultGraph = new CircularLayoutPrimesGraph(this.graph);
		defaultGraph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		defaultGraph.setSize(400, 320);
		defaultGraph.setVisible(true);
		defaultGraph.getRootPane().grabFocus();

		final var compactTreeGraph = new CompactTreeLayoutPrimesGraph(this.graph);
		compactTreeGraph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		compactTreeGraph.setSize(400, 320);
		compactTreeGraph.setVisible(true);
		compactTreeGraph.getRootPane().grabFocus();

		do
		{
			// will exit when window closes
		} while (System.in.read() != -1);
	}
}
