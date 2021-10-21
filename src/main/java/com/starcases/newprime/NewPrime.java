package com.starcases.newprime;


import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import lombok.extern.java.Log;

// 
// start 1
//       2
//       3 <-  2 + 1
//       5 <-  3 + 2
//       7 <-  5 + 2
//       11 <- 7+3+1; 5+3+2+1
@Log
public class NewPrime 
{
	private static Comparator<Node> node_comparator = new NaturalComparator();
	
	public static void main(String [] args)
	{
		// Setup up props for what UI systems are in use.
		System.setProperty("sun.java2d.opengl", "True");
		System.setProperty("org.graphstream.ui", "swing");
		
		// Allow multiple edges between nodes with "MultiGraph"
		Graph graph = new MultiGraph("Primes");
		
		// Setup CSS - overriding in most of the code at the moment.
		URL res = graph.getClass().getClassLoader().getResource("default.css");
		try
		{
			String stylesheet = "url(" + res.toURI() + ")";
			graph.setAttribute("ui.stylesheet", stylesheet);
		}
		catch(Exception e)
		{
			log.severe("Can't load default.css:" + e.toString()); 
		}
		
		// Setup up some items to improve visual output quality
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
		
		// Start setting up the actual graph/data generations
		PrimeGenerator pg = new PrimeGenerator(graph);
		pg.begin();
		
		// Current guava can't process more than 30 items to Powerset call.
		for (int i = 0; i < 30; i++)
		{
			pg.nextEvents();
		}			
		
		pg.end();
			
		// Dump a list of nodes showing degree in/out
		log.info("Degrees in / out");
		List<Node> degrees = Toolkit.degreeMap(graph);
		for (Node deg : degrees)
		{
			log.info(String.format(" Node %s In degrees %d, Out degrees %s", deg.getId(), deg.getInDegree(), deg.getOutDegree()));
		}
		
		log.info("bases");		
		degrees.stream().sorted(node_comparator).forEach(n -> log.info(String.format("Node %s : in-primes: [%s] out-primes[%s]", 
						n.getId(), 
						n.enteringEdges().map(e -> e.getNode0().getId()).collect(Collectors.joining(",")),
						n.leavingEdges().map(e -> e.getNode0().getId()).collect(Collectors.joining(",")))));
		
		// Setup / start viewing resulting graph
		Viewer viewer = graph.display(true);
		View view = viewer.getDefaultView();
	}
}
