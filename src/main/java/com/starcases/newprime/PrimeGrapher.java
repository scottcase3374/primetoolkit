package com.starcases.newprime;


import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
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
public class PrimeGrapher 
{
	private static Comparator<Node> nodeComparator = (Node o1, Node o2) -> Integer.decode(o1.getId()).compareTo(Integer.decode(o2.getId()));
	
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
		PrimeNodeGenerator pg = new PrimeNodeGenerator(graph);
		pg.begin();
		
		while (pg.nextEvents());		
		
		pg.end();
			
		List<Node> degrees = Toolkit.degreeMap(graph);	
		degrees.stream().sorted(nodeComparator).forEach(n -> log.info(String.format("Prime %s: created-from:[%s] creates-primes:[%s]", 
						n.getId(), 
						n.enteringEdges().map(e -> e.getSourceNode().getId()).collect(Collectors.joining(",")),
						n.leavingEdges().map(e -> e.getTargetNode().getId()).collect(Collectors.joining(",")))));
		
		degrees.stream().sorted(nodeComparator).forEach(n -> { 
																n.setAttribute("xyz", Integer.decode(n.getId()+3), n.getDegree(), 230 -Integer.decode(n.getId()));
															});
		
		// Setup / start viewing resulting graph
		Viewer viewer = graph.display(false);
		viewer.getDefaultView();
	}
}
