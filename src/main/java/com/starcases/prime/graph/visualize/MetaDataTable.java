package com.starcases.prime.graph.visualize;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import com.starcases.prime.intfc.PrimeRefIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;

/**
 * Visualization - table oriented
 *
 * Display some "meta-data" pulled/calc'ed/summarized from the graph data.
 *
 * Uses the graph listener interface to receive events on vertex/edge changes
 * and mostly uses those to calculate information that may be of interest.
 *
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals"})
public class MetaDataTable extends JFrame implements GraphListener<PrimeRefIntfc, DefaultEdge>
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * metric - Index into column/data fields
	 */
	private static final int PRIME_MAX_DIST_PREV_PRIME = 1;

	/**
	 * metric - index into column/data fields
	 */
	private static final int AVG_BASE_SIZE = 2;


	/**
	 * metric - index into column/data fields
	 */
	private static final int AVG_DIST_PREV_PRIME = 3;

	/**
	 * represent a set of column header values for various metrics
	 */
	@NonNull
	private static final String [] column = {
			"Prime / max num-bases (Default type)",
			"Prime / max dist to prev Prime",
			"avg base size",
			"avg dist to prev",
			"Highest Prime base"
	};

	/**
	 * represent a set of data valuesin a table for various metrics
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	@NonNull
	private String [][] data = { {"","","", "", ""}};

	/**
	 * metric - max distance to previous prime
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private PrimeRefIntfc primeMaxDistToPrev;

	/**
	 * metric - total base
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private BigDecimal totalBases = BigDecimal.ZERO;

	/**
	 * metric - highest prime base
	 */
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private PrimeRefIntfc highPrimeBase;

	/**
	 * UI element for metrics
	 */
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final JTable table;

	/**
	 * Create the frame.
	 */
	public MetaDataTable()
	{
		super();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		table = new JTable(data, column);
		final var scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		getContentPane().add(scrollPane);
	}

	/**
	 * Unused right now
	 * @param event
	 */
	protected void handlePrimeMaxBaseSize(@NonNull final GraphVertexChangeEvent<PrimeRefIntfc> event)
	{
		// unused right now
	}

	/**
	 * Calc the max distance primes in a base.
	 * @param event
	 */
	protected void handlePrimeMaxDistToPrevPrime(@NonNull final GraphVertexChangeEvent<PrimeRefIntfc> event)
	{
		if (primeMaxDistToPrev == null)
		{
			primeMaxDistToPrev = event.getVertex();
		}
		else
		{
			final var edist = event.getVertex().getDistToPrevPrime();
			final var pdist = primeMaxDistToPrev.getDistToPrevPrime();

			if (pdist.isEmpty() || edist.isPresent() && Math.abs(edist.getAsLong()) - Math.abs(pdist.getAsLong()) > 0)
			{
				primeMaxDistToPrev = event.getVertex();
			}
		}
		data[0][PRIME_MAX_DIST_PREV_PRIME] = String.format("Prime [%d] / max-dist[%d]", primeMaxDistToPrev.getPrime(), primeMaxDistToPrev.getDistToPrevPrime().orElse(0L));
	}

	/**
	 * Calc the base number of items for a base.
	 * @param event
	 */
	protected void handleAvgBaseSize(@NonNull final GraphVertexChangeEvent<PrimeRefIntfc> event)
	{
		data[0][AVG_BASE_SIZE] = String.format("# primes [%d], total-bases[%d] avg-bases[%f]", primeMaxDistToPrev.getPrimeRefIdx(), totalBases.longValue(), (double)totalBases.longValue() / (primeMaxDistToPrev.getPrimeRefIdx()+1));
	}

	/**
	 * Calc average dist from previous prime to next prime
	 */
	protected void handleAvgDistToPrev()
	{
		data[0][AVG_DIST_PREV_PRIME] = String.format("Total dist[%d], total-primes[%d] avg-dist[%f]", primeMaxDistToPrev.getPrime(), primeMaxDistToPrev.getPrimeRefIdx() , (double)primeMaxDistToPrev.getPrime() / (primeMaxDistToPrev.getPrimeRefIdx()+1));
	}

	/**
	 * Find base with most items
	 * @param event
	 */
	protected void handleHighPrimeBase(@NonNull final GraphVertexChangeEvent<PrimeRefIntfc> event)
	{
		if (highPrimeBase == null || event.getVertex().getPrimeBaseData().getPrimeBases().get(0).size() > highPrimeBase.getPrimeBaseData().getPrimeBases().get(0).size())
		{
			highPrimeBase = event.getVertex();
		}
	}

	@Override
	public void vertexAdded(@NonNull final GraphVertexChangeEvent<PrimeRefIntfc> event)
	{
		handlePrimeMaxBaseSize(event);
		handlePrimeMaxDistToPrevPrime(event);
		handleAvgBaseSize(event);
		handleAvgDistToPrev();
		handleHighPrimeBase(event);
	}

	@Override
	public void edgeAdded(final GraphEdgeChangeEvent<PrimeRefIntfc, DefaultEdge> event)
	{
		// Not handling any edge related logic right now.
	}

	@Override
	public void vertexRemoved(final GraphVertexChangeEvent<PrimeRefIntfc> event)
	{
		// No removal performed
	}

	@Override
	public void edgeWeightUpdated(@NonNull final GraphEdgeChangeEvent<PrimeRefIntfc, DefaultEdge> event)
	{
		GraphListener.super.edgeWeightUpdated(event);
	}

	@Override
	public void edgeRemoved(final GraphEdgeChangeEvent<PrimeRefIntfc, DefaultEdge> event)
	{
		// No removals performed
	}
}
