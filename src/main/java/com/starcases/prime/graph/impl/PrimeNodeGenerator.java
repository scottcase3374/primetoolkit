package com.starcases.prime.graph.impl;

import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;

import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;

/**
 * This is just an experiment with the GraphStream lib to 
 * see how well it works for something like my pet prime# 
 * research projects.
 */
@Log
public class PrimeNodeGenerator //extends SourceBase implements Generator 
{
	GraphModel graphModel;
	int level = 0;
	PrimeRefIntfc primeRef = null;
	PrimeSourceIntfc ps;
	DirectedGraph graph;
	
	public PrimeNodeGenerator(PrimeSourceIntfc ps, GraphModel graphModel, DirectedGraph graph)
	{
		this.ps = ps;
		this.graphModel = graphModel;
		this.graph = graph;
	}
	
	public void begin() 
	{	
		for (level = 0; level < 2; level++)
		{
			PrimeRefIntfc ref = ps.getPrimeRef(level);
			Node targetNode = graphModel.factory().newNode(ref.getPrime().toString());
			targetNode.setLabel(ref.getPrime().toString());
			Edge e = graphModel.factory().newEdge(targetNode, targetNode , true);
			graph.addNode(targetNode);
			graph.addEdge(e);
		}
		
	}

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
			log.info("dataset exhaused");
		}
		return false;
	}

	public void end() 
	{
		// Nothing to do
	}

	/**
	 * get a primeRef and add to graph.  
	 * set some attributes for data/visuals.
	 * Create edges from prime node to primes from the base sets representing the prime.
	 */
	protected void addNode() 
	{
		// Prime node
		String primeNodeLabel = primeRef.getPrime().toString();
		Node targetNode = graphModel.factory().newNode(primeNodeLabel);
		targetNode.setLabel(primeNodeLabel);
		
		// Link from prime node to prime bases
		primeRef.getPrimeBaseIdxs()
			.forEach(
					base ->  
							 base
							.stream()
							.forEach(
									p -> {
											Node sourceNode = graphModel.getNodeTable().getGraph().getNode(ps.getPrimeRef(p).getPrime().toString());
											graph.addNode(targetNode);
											Edge e = graphModel.factory().newEdge(sourceNode, targetNode , true);
											graph.addEdge(e);
										}));
		level++;
	}
}
