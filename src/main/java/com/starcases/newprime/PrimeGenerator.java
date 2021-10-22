package com.starcases.newprime;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.stream.SourceBase;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

/**
 * This is just an experiment with the GraphStream lib to 
 * see how well it works for something like my pet prime# 
 * research projects.
 */
public class PrimeGenerator extends SourceBase implements Generator {

	int edgeId = 1;
	SpriteManager sman;
	Graph graph;
	int level = 0;
	PrimeRef ref = null;
	public PrimeGenerator(Graph graph)
	{
		this.addSink(graph);
		sman = new SpriteManager(graph);
		this.graph = graph;
	}
	
	public void begin() 
	{}

	public boolean nextEvents() 
	{
		ref = PrimeRef.nextPrimeRef();
		if (ref != null)
		{
			addNode();
			return true;
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
		int r = 255 - level * 8;
		int g = 80 + level * 4;
		int b = 2 + level * 8;
		return  String.format("rgb(%d,%d,%d)", r,g,b);
	}
	
	/**
	 * get a prime ref and add to graph.  
	 * set some attributes for data/visuals.
	 * Create edges from prime node to primes from the base sets representing the prime.
	 */
	protected void addNode() 
	{
		String color = getColor();
		 
		// Prime node
		String primeNode = Long.toString(ref.getPrime());
		sendNodeAdded(sourceId, primeNode);
	
		Sprite s = sman.addSprite(primeNode);
		s.attachToNode(primeNode);
		s.setAttribute("ui.style","fill-color: " + color + ";");
		s.setAttribute("ui.label", primeNode);
		
		// Link from prime node to prime bases
		ref.primeBases.stream()
			.forEach(
					base ->  
							 base
							.stream()
							.forEach(
									p -> { 
											sendEdgeAdded(	sourceId, 
														Integer.toString(edgeId), 														
														Long.toString(p.getPrime()),
														primeNode,  
														true);
											Edge e = graph.getEdge(Integer.toString(edgeId++));
											e.setAttribute("ui.style", 
													"fill-color: " + color + ";");
										}));
		level++;
	}
}
