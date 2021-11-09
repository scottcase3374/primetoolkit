package com.starcases.prime.graph.impl;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.stream.SourceBase;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;

/**
 * This is just an experiment with the GraphStream lib to 
 * see how well it works for something like my pet prime# 
 * research projects.
 */
@Log
public class PrimeNodeGenerator extends SourceBase implements Generator 
{
	int edgeId = 1;
	SpriteManager sman;
	Graph graph;
	int level = 0;
	PrimeRefIntfc primeRef = null;
	
	PrimeSourceIntfc ps;
	
	public PrimeNodeGenerator(PrimeSourceIntfc ps, Graph graph)
	{
		this.ps = ps;
		
		this.addSink(graph);
		sman = new SpriteManager(graph);
		this.graph = graph;
	}
	
	public void begin() 
	{}

	public boolean nextEvents() 
	{
		try
		{
			primeRef = ps.getPrimeRef(level);
			if (primeRef != null)
			{
				//log.info("nextEvents() produced prime " + primeRef.getPrime() + " for level " + level);
				addNode();
				return true;
			}
		}
		catch(IndexOutOfBoundsException e)
		{
			// do nothing - final return handles it.
		}
		return false;
	}

	public void end() 
	{
		// Nothing to do
	}

	/**
	 * Could experiment with some additional criteria to generate some more 
	 * dynamic values for colors of nodes, edges, etc.
	 * 
	 * @return String in form of "rgb(r#,g#,b#)"
	 */
	protected String getColor()
	{
		int level = 2;
		int r = 255 - level * 8;
		int g = 80 + level * 4;
		int b = 2 + level * 8;
		return  String.format("rgb(%d,%d,%d)", r,g,b);
	}
	
	/**
	 * get a primeRef and add to graph.  
	 * set some attributes for data/visuals.
	 * Create edges from prime node to primes from the base sets representing the prime.
	 */
	protected void addNode() 
	{
		String color = getColor();
		 
		// Prime node
		String primeNode = primeRef.getPrime().toString();
		sendNodeAdded(sourceId, primeNode);
	
		Sprite s = sman.addSprite(primeNode);
		s.attachToNode(primeNode);
		s.setAttribute("ui.style","fill-color: " + color + ";");
		s.setAttribute("ui.label", primeNode);
		
		// Link from prime node to prime bases
		primeRef.getPrimeBaseIdxs()
			.forEach(
					base ->  
							 base
							.stream()
							.forEach(
									p -> {
											sendEdgeAdded(	sourceId, 
														Integer.toString(edgeId), 														
														ps.getPrime(p).toString(),
														primeNode,  
														true);
											Edge e = graph.getEdge(Integer.toString(edgeId++));
											e.setAttribute("ui.style", 
													"fill-color: " + color + ";");
											e.setAttribute("ui.style", "shape: cubic-curve;");
										}));
		level++;
	}
}
