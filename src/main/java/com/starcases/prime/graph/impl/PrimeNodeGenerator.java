package com.starcases.prime.graph.impl;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphBuilder;

import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;

/**
 * This is just an experiment with the GraphStream lib to 
 * see how well it works for something like my pet prime# 
 * research projects.
 */
@Log
public class PrimeNodeGenerator
{
	
	int level = 0;
	PrimeRefIntfc primeRef = null;
	PrimeSourceIntfc ps;
	GraphBuilder<String, DefaultEdge, DefaultDirectedGraph<String, DefaultEdge>> graph;
	
	public PrimeNodeGenerator(PrimeSourceIntfc ps, GraphBuilder<String, DefaultEdge, DefaultDirectedGraph<String, DefaultEdge>> graph)
	{
		this.ps = ps;
		this.graph = graph;
	}
	
	public void begin() 
	{	
		for (level = 0; level < 2; level++)
		{
			PrimeRefIntfc targetNode = ps.getPrimeRef(level);
			graph.addVertex(targetNode.getPrime().toString());
			String targetNodeId = targetNode.getPrime().toString();
			graph.addEdge(targetNodeId, targetNodeId);
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

	/**
	 * get a primeRef and add to graph.  
	 * Create edges from prime node to primes from the base sets representing the prime.
	 */
	protected void addNode() 
	{	
		// Link from prime node to prime bases
		primeRef.getPrimeBaseIdxs()
			.forEach(
					base ->  
							 base
							.stream()
							.forEach(
									p -> {											
											String targetNodeId = primeRef.getPrime().toString();
											graph.addVertex(targetNodeId);
											String sourceNodeId = ps.getPrimeRef(p).getPrime().toString();
											graph.addEdge(sourceNodeId, targetNodeId);
										}));
		level++;
	}
}
