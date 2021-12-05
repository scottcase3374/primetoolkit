package com.starcases.prime.graph.visualize;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import com.starcases.prime.intfc.PrimeRefIntfc;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;

/**
 * Display 
 * 	prime/max-base-size
 * 	prime/max dist to prev prime
 *  avg base size
 *  
 * @author scott
 *
 */
public class MetaDataTable extends JFrame implements GraphListener<PrimeRefIntfc, DefaultEdge> 
{
	String column[]={"prime/max-base-size","prime/max dist to prev prime","avg base size"};  
	
	String data[][]={ {"","",""}};
	
	private JTable table;
	/**
	 * Create the frame.
	 */
	public MetaDataTable() 
	{
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		table = new JTable(data, column);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		getContentPane().add(scrollPane);
	}

	@Override
	public void vertexAdded(GraphVertexChangeEvent<PrimeRefIntfc> e) 
	{
		data[0][0]= e.getVertex().getPrime().toString();
		
		System.out.println("***** meta data vertex added");
		
	}
	
	@Override
	public void edgeAdded(GraphEdgeChangeEvent<PrimeRefIntfc, DefaultEdge> e) 
	{
		System.out.println("***** meta data edge added");
		
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
