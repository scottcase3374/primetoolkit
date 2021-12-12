package com.starcases.prime.graph.visualize;

import javax.swing.WindowConstants;

import org.jgrapht.event.GraphListener;

import com.starcases.prime.graph.impl.PrimeGrapher;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.intfc.BaseTypes;
import lombok.extern.java.Log;

/**
 * Default graphing entry point - provides cicular layout and compact tree layout based on
 * same underlying data.
 * 
 * supports inclusion of other visuals/displays (such as MetaData Table) via GraphListener.
 */
@Log
public class ViewDefault extends PrimeGrapher
{	
	
	public ViewDefault(PrimeSourceIntfc ps, BaseTypes baseType, GraphListener...graphs)
	{
		super(ps, log, baseType, graphs);
	}
	
	public void viewDefault()
	{
        try
		{
			var defaultGraph = new CircularLayoutPrimesGraph(this.graph);
			defaultGraph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			defaultGraph.setSize(400, 320);
			defaultGraph.setVisible(true);	
			defaultGraph.getRootPane().grabFocus();
			
			var compactTreeGraph = new CompactTreeLayoutPrimesGraph(this.graph);
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
