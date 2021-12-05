package com.starcases.prime.graph.visualize;

import javax.swing.WindowConstants;

import org.jgrapht.event.GraphListener;

import com.starcases.prime.graph.impl.PrimeGrapher;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;

@Log
public class ViewDefault extends PrimeGrapher
{	
	
	public ViewDefault(PrimeSourceIntfc ps, GraphListener...graphs)
	{
		super(ps, log, graphs);
	}
	
	public void viewDefault()
	{
        try
		{
			CircularLayoutPrimesGraph defaultGraph = new CircularLayoutPrimesGraph(this.graph);
			defaultGraph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			defaultGraph.setSize(400, 320);
			defaultGraph.setVisible(true);	
			defaultGraph.getRootPane().grabFocus();
			
			CompactTreeLayoutPrimesGraph compactTreeGraph = new CompactTreeLayoutPrimesGraph(this.graph);
			compactTreeGraph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			compactTreeGraph.setSize(400, 320);
			compactTreeGraph.setVisible(true);	
			compactTreeGraph.getRootPane().grabFocus();			
			
			do
			{
				// will exit when window closes
			} while (System.in.read() != -1);
		}
		catch(Exception e)
		{
			log.severe("Exception:" + e);
		}	        
	}
}
