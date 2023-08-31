package com.starcases.prime.graph.impl;


import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.Graph;

import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
public class PrimeNodeGenerator
{
	/**
	 * roughly equivalent to index of prime
	 */
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	protected int level;

	/**
	 * Ref to prime info
	 */
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	protected PrimeRefIntfc primeRef;

	/**
	 * Lookup prime/prime refs
	 */
	@NonNull
	@Getter(AccessLevel.PROTECTED)
	protected final PrimeSourceIntfc primeSrc;

	/**
	 * maintain graph structure
	 */
	@NonNull
	@Getter(AccessLevel.PROTECTED)
	protected final Graph<PrimeRefIntfc, DefaultEdge> graph;

	/**
	 * Base used
	 */
	@NonNull
	@Getter(AccessLevel.PROTECTED)
	protected final BaseTypesIntfc baseType;

	/**
	 * constructor for the node generator
	 * @param primeSrc
	 * @param graph
	 * @param baseType
	 */
	public PrimeNodeGenerator(@NonNull final  PrimeSourceIntfc primeSrc, final Graph<PrimeRefIntfc, DefaultEdge> graph, @NonNull final BaseTypesIntfc baseType)
	{
		this.primeSrc = primeSrc;
		this.graph = graph;
		this.baseType = baseType;
	}

	/**
	 * Begin producing nodes
	 */
	public void begin()
	{
		// bootstrap
		for (level = 0; level < 2; level++)
		{
			primeSrc.getPrimeRefForIdx(level).ifPresent(
					targetNode ->
									{
										graph.addVertex(targetNode);
										graph.addEdge(targetNode, targetNode);
									}
					);
		}
	}

	/**
	 * Continue producing events/nodes after initial bootstrap
	 * @return
	 */
	public boolean nextEvents()
	{
		final boolean more[] = { false };
		try
		{
			primeSrc.getPrimeRefForIdx(level).ifPresent(pRef ->
				{
					primeRef = pRef;
					addNodeRawBase();
					more[0] = true;
				});
		}
		catch(final NullPointerException e)
		{
			more[0] = false;
		}
		return more[0];
	}

	/**
	 * get a primeRef and add to graph.
	 * Create edges from Prime node to primes from the base sets representing the Prime.
	 */
	protected void addNodeRawBase()
	{
		// Link from Prime node to Prime bases (i.e. unique set of smaller primes that sums to this Prime).
		primeRef
			.getPrimeBaseData()
			.getPrimeBases(baseType)
			 .get(0)
			.forEach(
					base -> {
							addVertex(primeRef);
							addBaseEdge(base, primeRef);
						});
		level++;
	}

	/**
	 * Add new vertex for prime/ref
	 * @param primeRef
	 */
	protected void addVertex(final PrimeRefIntfc primeRef)
	{
		graph.addVertex(primeRef);
	}

	/**
	 * Add edge for prime/pref sourced from specified base
	 * @param base
	 * @param primeRef
	 */
	protected void addBaseEdge(final long base, final PrimeRefIntfc primeRef)
	{
		primeSrc.getPrimeRefForPrime(base).ifPresent( p -> graph.addEdge(p , primeRef));
	}
}
