package com.starcases.prime;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

import com.starcases.prime.graph.impl.PrimeNodeGenerator;

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
		int targetRows = 1500;
		if (args != null && args[0] != null)
		{
			try
			{
				targetRows = Integer.parseInt(args[0]);
			}
			catch(Exception e)
			{}
		}
		PrimeGrapher primeGrapher = new PrimeGrapher();		
		boolean debug = true;
		primeGrapher.populateData(targetRows, debug);
		//primeGrapher.logGraphStructure();
		primeGrapher.setNodeLocations();
		primeGrapher.viewDefault();
	}

	private void setNodeLocations()
	{
		List<Node> degrees = Toolkit.degreeMap(primeGraph);	
		degrees.stream().sorted(nodeComparator).forEach(n ->  
			/*n.setAttribute("xyz", 80 - Math.sin(11 * Integer.decode(n.getId()))*127,								   
								  (n.getOutDegree()* 11 + Math.sin(Integer.decode(n.getId()))) + 
								    	(n.getInDegree() * 11 + Math.cos(Integer.decode(n.getId()))) 
								 ,0)*/
		n.setAttribute("xyz", 								   
				30 + 400 * (Math.log10(Integer.decode(n.getId())) + n.getInDegree()) * Math.signum(Math.cos(n.getInDegree()*11)),  
				80 + 31 * n.getOutDegree() * Math.sin(n.getOutDegree()), 				     
				80 + 109 * Math.cos(Integer.decode(n.getId()) ))
	 	); // Integer.decode(n.getId()
	}
	

	
	private void logGraphStructure()
	{
		List<Node> degrees = Toolkit.degreeMap(primeGraph);	
		degrees.stream().sorted(nodeComparator).forEach(n -> log.info(String.format("Prime %s: created-from:[%s] creates-primes:[%s]", 
						n.getId(), 
						n.enteringEdges().map(e -> e.getSourceNode().getId()).collect(Collectors.joining(",")),
						n.leavingEdges().map(e -> e.getTargetNode().getId()).collect(Collectors.joining(",")))));		
	}
	
	private void populateData(int targetRows, boolean debug)
	{
		var level = log.getLevel();
		if (debug)
			log.setLevel(level.FINE);
		else
			log.setLevel(level.SEVERE);
		
		// Start setting up the actual graph/data generations
		PrimeNodeGenerator primeNodeGenerator = new PrimeNodeGenerator(this.primeGraph, targetRows);
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
