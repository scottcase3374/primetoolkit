package com.starcases.prime.graph.impl;


import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.Graph;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;

import java.util.NoSuchElementException;

/**
 * This is just an experiment with the GraphStream lib originally 
 * and converted to jgrapht at the moment.
 *  
 * Goal: see how well it works for something like this prime# 
 * research project.
 * 
 * 
 */
@Log
public class PrimeNodeGenerator
{
	int level = 0;
	PrimeRefIntfc primeRef = null;
	PrimeSourceIntfc ps;
	Graph<PrimeRefIntfc, DefaultEdge> graph;
	BaseTypes baseType;
	
	public PrimeNodeGenerator(PrimeSourceIntfc ps, Graph<PrimeRefIntfc, DefaultEdge> graph, BaseTypes baseType)
	{
		this.ps = ps;
		this.graph = graph;
		this.baseType = baseType;
	}
	
	public void begin() 
	{	
		// bootstrap
		for (level = 0; level < 2; level++)
		{
			var targetNode = ps.getPrimeRef(level).get();

			graph.addVertex(targetNode);
			graph.addEdge(targetNode, targetNode);
		}
	}

	public boolean nextEvents() 
	{
		try
		{
			primeRef = ps.getPrimeRef(level).orElseThrow(); 			
			addNode();
			return true;
		}
		catch(NoSuchElementException | IndexOutOfBoundsException | NullPointerException e)
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
		// Link from prime node to prime bases (i.e. unique set of smaller primes that sums to this prime).
		primeRef.getPrimeBaseIdxs(baseType)
							.stream()
							.forEach(
									baseIdx -> {	
											addVertext(primeRef);
											addBaseEdge(primeRef, baseIdx);
										});
		level++;
	}

	protected void addVertext(PrimeRefIntfc primeRef)
	{
		graph.addVertex(primeRef);		
	}
	
	protected void addBaseEdge(PrimeRefIntfc primeRef, int baseIdx)
	{
		graph.addEdge( ps.getPrimeRef(baseIdx).get(), primeRef);
	}
}
