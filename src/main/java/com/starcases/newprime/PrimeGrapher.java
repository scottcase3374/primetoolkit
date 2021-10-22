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

	private Graph primeGraph;
	
	public static void main(String [] args)
	{
		PrimeGrapher primeGrapher = new PrimeGrapher();		
		boolean debug = true;
		primeGrapher.populateData(debug);
		primeGrapher.logGraphStructure();
		primeGrapher.setNodeLocations();
		primeGrapher.viewDefault();
	}

	private void setNodeLocations()
	{
		List<Node> degrees = Toolkit.degreeMap(primeGraph);	
		degrees.stream().sorted(nodeComparator).forEach(n -> { 
			n.setAttribute("xyz", Integer.decode(n.getId()+3), n.getDegree(), 230 -Integer.decode(n.getId()));
		});
	}
	

	
	private void logGraphStructure()
	{
		List<Node> degrees = Toolkit.degreeMap(primeGraph);	
		degrees.stream().sorted(nodeComparator).forEach(n -> log.info(String.format("Prime %s: created-from:[%s] creates-primes:[%s]", 
						n.getId(), 
						n.enteringEdges().map(e -> e.getSourceNode().getId()).collect(Collectors.joining(",")),
						n.leavingEdges().map(e -> e.getTargetNode().getId()).collect(Collectors.joining(",")))));		
	}
	
	private void populateData(boolean debug)
	{
		var level = log.getLevel();
		if (debug)
			log.setLevel(level.FINE);
		else
			log.setLevel(level.SEVERE);
		
		// Start setting up the actual graph/data generations
		PrimeNodeGenerator primeNodeGenerator = new PrimeNodeGenerator(this.primeGraph);
		primeNodeGenerator.begin();
		
		while (primeNodeGenerator.nextEvents());		
		
		primeNodeGenerator.end();
		log.setLevel(level);
	}

	private void init()
	{
		setUIProperties();
		this.primeGraph = genEmptyGraphStructure();
		setupDefaultVisualPropertiesAndCSS();
	}

	PrimeGrapher()
	{
		init();
	}
	
	private void setupDefaultVisualPropertiesAndCSS()
	{
		// Setup CSS - overriding in most of the code at the moment.
		URL res = primeGraph.getClass().getClassLoader().getResource("default.css");
		try
		{
			String stylesheet = "url(" + res.toURI() + ")";
			primeGraph.setAttribute("ui.stylesheet", stylesheet);
		}
		catch(Exception e)
		{
			log.severe("Can't load default.css:" + e.toString()); 
		}
		
		// Setup up some items to improve visual output quality
		primeGraph.setAttribute("ui.quality");
		primeGraph.setAttribute("ui.antialias");		
	}

	void viewDefault()
	{
		// Setup / start viewing resulting graph
		Viewer viewer = primeGraph.display(false);
		viewer.getDefaultView();		
	}

	private Graph genEmptyGraphStructure()
	{
		// Allow multiple edges between nodes with "MultiGraph"
		return new MultiGraph("Primes");
	}
	
	private void setUIProperties()
	{
		// Setup up props for what UI systems are in use.
		System.setProperty("sun.java2d.opengl", "True");
		System.setProperty("org.graphstream.ui", "swing");	
	}
}
