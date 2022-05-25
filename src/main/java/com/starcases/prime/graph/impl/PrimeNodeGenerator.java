package com.starcases.prime.graph.impl;


import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.Graph;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigInteger;

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
@SuppressWarnings({"PMD.CommentSize", "PMD.AvoidDuplicateLiterals"})
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
	protected final BaseTypes baseType;

	/**
	 * constructor for the node generator
	 * @param primeSrc
	 * @param graph
	 * @param baseType
	 */
	public PrimeNodeGenerator(@NonNull final  PrimeSourceIntfc primeSrc, final Graph<PrimeRefIntfc, DefaultEdge> graph, @NonNull final BaseTypes baseType)
	{
		this.primeSrc = primeSrc;
		this.graph = graph;
		this.baseType = baseType;
	}

	/**
	 * Begin producing nodes
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	public void begin()
	{
		// bootstrap
		for (level = 0; level < 2; level++)
		{
			primeSrc.getPrimeRef(level).ifPresent(
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
	@SuppressWarnings("PMD.LawOfDemeter")
	public boolean nextEvents()
	{
		primeSrc.getPrimeRef(level).ifPresent(pRef ->
			{
				primeRef = pRef;
				addNodeRawBase();
			});

		return true;
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

	/**
	 * Add new vertex for prime/ref
	 * @param primeRef
	 */
	protected void addVertext(final PrimeRefIntfc primeRef)
	{
		graph.addVertex(primeRef);
	}

	/**
	 * Add edge for prime/pref sourced from specified base
	 * @param base
	 * @param primeRef
	 */
	@SuppressWarnings("PMD.LawOfDemeter")
	protected void addBaseEdge(final BigInteger base, final PrimeRefIntfc primeRef)
	{
		primeSrc.getPrimeRef(base).ifPresent( p -> graph.addEdge(p , primeRef));
	}
}
