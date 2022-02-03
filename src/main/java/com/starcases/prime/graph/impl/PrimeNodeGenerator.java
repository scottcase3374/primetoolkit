package com.starcases.prime.graph.impl;


import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.Graph;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.NoSuchElementException;

/**
 * This was just an experiment with the GraphStream lib originally and then converted to jgrapht.
 * After that, the idea to create a more "tool kit" feeling came to mind and I started to refactor
 * it in support of that.  There are a number of classes/interfaces/relationships I would
 * change if I started completely over but this accomplishes what I desired at a minimal level
 * even though it could be done better  yet.
 *
 * Goal: Work with some graph and visualization support to understand capabilities.
 *
 *
 */
@Log
public class PrimeNodeGenerator
{
	int level = 0;

	PrimeRefIntfc primeRef = null;

	@NonNull
	PrimeSourceIntfc ps;

	@NonNull
	Graph<PrimeRefIntfc, DefaultEdge> graph;

	@NonNull
	BaseTypes baseType;

	public PrimeNodeGenerator(@NonNull PrimeSourceIntfc ps, Graph<PrimeRefIntfc, DefaultEdge> graph, @NonNull BaseTypes baseType)
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
			ps.getPrimeRef(level).ifPresent(
					targetNode ->
									{
										graph.addVertex(targetNode);
										graph.addEdge(targetNode, targetNode);
									}
					);
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
		}
		return false;
	}

	/**
	 * get a primeRef and add to graph.
	 * Create edges from prefixPrime node to primes from the base sets representing the prefixPrime.
	 */
	protected void addNode()
	{
		// Link from prefixPrime node to prefixPrime bases (i.e. unique set of smaller primes that sums to this prefixPrime).
		primeRef.getPrimeBaseData().getPrimeBaseIdxs(baseType)
							.get(0)
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
		ps.getPrimeRef(baseIdx).ifPresent( p -> graph.addEdge(p , primeRef));
	}
}
