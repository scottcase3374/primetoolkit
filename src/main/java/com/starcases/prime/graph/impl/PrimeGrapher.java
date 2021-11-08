package com.starcases.prime.graph.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

import com.starcases.prime.intfc.PrimeRefIntfc;
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
public class PrimeGrapher 
{
	private static Comparator<Node> nodeComparator = (Node o1, Node o2) -> Integer.decode(o1.getId()).compareTo(Integer.decode(o2.getId()));

	private Graph primeGraph;
	private PrimeSourceIntfc ps;
	
	public PrimeGrapher(PrimeSourceIntfc ps, int maxCount)
	{
		this.ps = ps;
		init();
		this.populateData(maxCount);
	}
	
	private void init()
	{
		setUIProperties();
		this.primeGraph = genEmptyGraphStructure();
		setupDefaultVisualPropertiesAndCSS();
	}
	
	public void setNodeLocations()
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
	
	@Command
	public void logGraphStructure()
	{
		List<Node> degrees = Toolkit.degreeMap(primeGraph);	
		degrees.stream().sorted(nodeComparator).forEach(n -> log.info(String.format("Prime %s: created-from:[%s] creates-primes:[%s]", 
						n.getId(), 
						n.enteringEdges().map(e -> e.getSourceNode().getId()).collect(Collectors.joining(",")),
						n.leavingEdges().map(e -> e.getTargetNode().getId()).collect(Collectors.joining(",")))));		
	}
	
	public void logNodeStructure()
	{
		try
		{
			int i = 0;
			while(true) 
			{ 
				PrimeRefIntfc ref = ps.getPrimeRef(i++);
				log.info(String.format("Prime %d bases %s", ref.getPrime(), ref.getIdxPrimes()));
			}
		}
		catch(Exception e)
		{}
	}
	
	final BiFunction<Integer, ArrayList<Integer>, Consumer<Integer>> reducer = (m, a)-> idx -> 
	{
		if (idx < m)
		{
			while (m > a.size())
				a.add(0);
			
			a.set(idx, a.get(idx)+1);
		}
		else 
		{ 
			this.primeReduction(idx, this.reducer.apply(m, a)); 
		}
	};
	
	 
	public void logReduced(int maxReduce)
	{
		int i = 0;
		while(true) 
		{ 
			PrimeRefIntfc pr;				
			try
			{
				ArrayList<Integer> ret = new ArrayList<>();
				pr = ps.getPrimeRef(i);
				primeReduction(i++, reducer.apply(maxReduce, ret));
				int [] tmpI = {0};			
				log.info(String.format("Prime [%d] %s", pr.getPrime(), 
						ret.stream().map(idx -> String.format("base-%d-count:[%d]", ps.getPrime(tmpI[0]++), idx)).collect(Collectors.joining(", "))));
			}
			catch(Exception e)
			{
				break;
			}				
		}	
	}
	
	private void primeReduction(Integer idx, Consumer<Integer> reducer)
	{	
		ps.getPrimeRef(idx)
		.getPrimeBaseIdxs()
		.forEach(
				bs -> 
					bs
					.stream()
					.boxed()
					.forEach(reducer));
	}
	
	private void populateData(int maxCount)
	{
		// Start setting up the actual graph/data generations
		PrimeNodeGenerator primeNodeGenerator = new PrimeNodeGenerator(ps, primeGraph, maxCount);
		primeNodeGenerator.begin();
		
		while (primeNodeGenerator.nextEvents());		
		
		primeNodeGenerator.end();		
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

	public void viewDefault()
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
