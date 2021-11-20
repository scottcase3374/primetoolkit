package com.starcases.prime.graph.impl;

import javax.swing.WindowConstants;
import com.starcases.prime.graph.visualize.VisualizeGraph;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

// 
// start 1
//       2
//       3 <-  2 + 1
//       5 <-  3 + 2
//       7 <-  5 + 2
//       11 <- 7+3+1; 5+3+2+1
@Log
public class ViewDefault extends PrimeGrapher
{	
	public ViewDefault(PrimeSourceIntfc ps)
	{
		super(ps, log);
	}
	
	public void viewDefault()
	{
        try
		{
			VisualizeGraph frame = new VisualizeGraph(this.graph);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setSize(400, 320);
			frame.setVisible(true);	
			frame.getRootPane().grabFocus();
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
