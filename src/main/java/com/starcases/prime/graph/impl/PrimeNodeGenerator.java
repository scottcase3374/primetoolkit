package com.starcases.prime.graph.impl;


import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.Graph;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

import java.math.BigInteger;
import java.util.NoSuchElementException;

/**
 * This was just an experiment with the GraphStream lib originally
 *  and then converted to jgrapht.
 * After that, the idea to create a more "tool kit" feeling came to
 * mind and I started to refactor
 * it in support of that.  There are a number of
 * classes/interfaces/relationships I would
 * change if I started completely over but this accomplishes
 * what I desired at a minimal level
 * even though it could be done better  yet.
 *
 * Goal: Work with some graph and visualization
 * support to understand capabilities.
 *
 *
 */
@SuppressWarnings({"PMD.CommentSize"})
public class PrimeNodeGenerator
{
	protected int level;

	protected PrimeRefIntfc primeRef;

	@NonNull
	protected final PrimeSourceIntfc ps;

	@NonNull
	protected final Graph<PrimeRefIntfc, DefaultEdge> graph;

	@NonNull
	protected final BaseTypes baseType;

	public PrimeNodeGenerator(@NonNull final  PrimeSourceIntfc ps, final Graph<PrimeRefIntfc, DefaultEdge> graph, @NonNull final BaseTypes baseType)
	{
		this.ps = ps;
		this.graph = graph;
		this.baseType = baseType;
	}

	@SuppressWarnings("PMD.LawOfDemeter")
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

	@SuppressWarnings("PMD.LawOfDemeter")
	public boolean nextEvents()
	{
		try
		{
			primeRef = ps.getPrimeRef(level).orElseThrow();
			addNodeRawBase();
			return true;
		}
		catch(final NoSuchElementException | IndexOutOfBoundsException | NullPointerException e)
		{
			// do nothing - final return handles it.
		}
		return false;
	}

	/**
	 * get a primeRef and add to graph.
	 * Create edges from Prime node to primes from the base sets representing the Prime.
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	protected void addNodeRawBase()
	{
		// Link from Prime node to Prime bases (i.e. unique set of smaller primes that sums to this Prime).
		primeRef.getPrimeBaseData().getPrimeBases(baseType)
							.get(0)
							.stream()
							.forEach(
									base -> {
											addVertext(primeRef);
											addBaseEdge(base, primeRef);
										});
		level++;
	}

	protected void addVertext(final PrimeRefIntfc primeRef)
	{
		graph.addVertex(primeRef);
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	protected void addBaseEdge(final BigInteger base, final PrimeRefIntfc primeRef)
	{
		ps.getPrimeRef(base).ifPresent( p -> graph.addEdge(p , primeRef));
	}
}
