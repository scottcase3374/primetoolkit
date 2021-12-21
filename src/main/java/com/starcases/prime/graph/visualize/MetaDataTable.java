package com.starcases.prime.graph.visualize;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;

/**
 * Display some "meta-data" pulled/calc'ed/summarized from the graph data.
 * 
 * Uses the graph listener interface to receive events on vertex/edge changes
 * and mostly uses those to calculate information that may be of interest.
 *
 */
public class MetaDataTable extends JFrame implements GraphListener<PrimeRefIntfc, DefaultEdge> 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static final int PRIME_MAX_BASE_SIZE = 0;
	static final int PRIME_MAX_DIST_PREV_PRIME = 1;
	static final int AVG_BASE_SIZE = 2;
	static final int AVG_DIST_PREV_PRIME = 3;
	static final int MAX_PRIME_BASE = 4;
	
	String [] column = {
			"prime / max num-bases (Default type)",
			"prime / max dist to prev prime",
			"avg base size",
			"avg dist to prev",
			"Highest prime base"
	};  
	
	String [][] data = { {"","","", "", ""}};

	transient PrimeRefIntfc primeMaxBaseSize = null;
	transient PrimeRefIntfc primeMaxDistToPrev = null;
	transient BigDecimal totalBases = BigDecimal.ZERO;
	transient PrimeRefIntfc highPrimeBase = null;

	private JTable table;
	/**
	 * Create the frame.
	 */
	public MetaDataTable() 
	{
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		table = new JTable(data, column);
		var scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		getContentPane().add(scrollPane);	
	}

	protected void handlePrimeMaxBaseSize(GraphVertexChangeEvent<PrimeRefIntfc> e)
	{
		if (primeMaxBaseSize == null || e.getVertex().getBaseSize() > primeMaxBaseSize.getBaseSize())
		{
			primeMaxBaseSize = e.getVertex();	
		}
		
		data[0][PRIME_MAX_BASE_SIZE]= String.format("Prime [%d] / Base# [%d] / Bases [%s]", primeMaxBaseSize.getPrime(), primeMaxBaseSize.getBaseSize(), primeMaxBaseSize.getIdxPrimes(BaseTypes.DEFAULT));
		
	}

	protected void handlePrimeMaxDistToPrevPrime(GraphVertexChangeEvent<PrimeRefIntfc> e)
	{
		if (primeMaxDistToPrev == null)
		{
			primeMaxDistToPrev = e.getVertex();	
		}
		else 
		{
			var edist = e.getVertex().getDistToPrevPrime();
			var pdist = primeMaxDistToPrev.getDistToPrevPrime();
			if (pdist.isEmpty())
			{
				primeMaxDistToPrev = e.getVertex();
			}
			else if (edist.isPresent())
			{
				if (edist.get().abs().compareTo(pdist.get().abs()) > 0)
					primeMaxDistToPrev = e.getVertex();
			} 
		}
		data[0][PRIME_MAX_DIST_PREV_PRIME] = String.format("Prime [%d] / max-dist[%d]", primeMaxDistToPrev.getPrime(), primeMaxDistToPrev.getDistToPrevPrime().orElse(BigInteger.ZERO));	
	}
	
	protected void handleAvgBaseSize(GraphVertexChangeEvent<PrimeRefIntfc> e)
	{
		totalBases = totalBases.add(BigDecimal.valueOf(e.getVertex().getBaseSize()));
		data[0][AVG_BASE_SIZE] = String.format("# primes [%d], total-bases[%d] avg-bases[%f]", primeMaxDistToPrev.getPrimeRefIdx(), totalBases.longValue(), ((double)totalBases.longValue() / (primeMaxDistToPrev.getPrimeRefIdx()+1))); 
	}

	protected void handleAvgDistToPrev()
	{		
		data[0][AVG_DIST_PREV_PRIME] = String.format("Total dist[%d], total-primes[%d] avg-dist[%f]", primeMaxDistToPrev.getPrime().longValue(), primeMaxDistToPrev.getPrimeRefIdx() , ((double)primeMaxDistToPrev.getPrime().longValue() / (primeMaxDistToPrev.getPrimeRefIdx()+1))); 
	}
	
	protected void handleHighPrimeBase(GraphVertexChangeEvent<PrimeRefIntfc> e)
	{
		if (highPrimeBase == null || e.getVertex().getPrimeBaseIdxs().length() > highPrimeBase.getPrimeBaseIdxs().length())
		{
			highPrimeBase = e.getVertex();	
		}
		data[0][MAX_PRIME_BASE] = String.format("Prime[%d], Highest base[%d]", highPrimeBase.getPrime(), highPrimeBase.getMaxPrimeBase());
	}

	@Override
	public void vertexAdded(GraphVertexChangeEvent<PrimeRefIntfc> e) 
	{
		handlePrimeMaxBaseSize(e);
		handlePrimeMaxDistToPrevPrime(e);
		handleAvgBaseSize(e);
		handleAvgDistToPrev();	
		handleHighPrimeBase(e);
	}
	
	@Override
	public void edgeAdded(GraphEdgeChangeEvent<PrimeRefIntfc, DefaultEdge> e) 
	{
		// Not handling any edge related logic right now.	
	}

	@Override
	public void vertexRemoved(GraphVertexChangeEvent<PrimeRefIntfc> e) 
	{
		// No removal performed
	}

	@Override
	public void edgeWeightUpdated(GraphEdgeChangeEvent<PrimeRefIntfc, DefaultEdge> e) 
	{
		GraphListener.super.edgeWeightUpdated(e);
	}


	@Override
	public void edgeRemoved(GraphEdgeChangeEvent<PrimeRefIntfc, DefaultEdge> e) 
	{
		// No removals performed
	}
}