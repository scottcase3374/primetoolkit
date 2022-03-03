package com.starcases.prime.graph.log;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.graph.impl.PrimeGrapher;
import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import picocli.CommandLine.Command;

/**
 * Pulls data from the graph structure and logs it. Main
 * distinctions is the use of the jgrapht api calls to access
 * the main data.
 *
 */
public class LogGraphStructure extends PrimeGrapher implements LogGraphIntfc
{
	private static final Logger log = Logger.getLogger(LogGraphStructure.class.getName());

	public LogGraphStructure(@NonNull PrimeSourceIntfc ps, @NonNull BaseTypes baseType)
	{
		super(ps, baseType);
	}

	private boolean preferParallel;
	public LogGraphIntfc doPreferParallel(boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	@Override
	@Command
	public void l()
	{
		log.entering("LogGraphStructure", "log()");
			graph
				.vertexSet()
				.stream()
				.sorted(nodeComparator)
				.forEach(n ->
							System.out.println(String.format("Prime %s: created-from:[count(%d), %s] creates-primes:[count(%d), %s]",
						n,
						graph.inDegreeOf(n),
						graph.incomingEdgesOf(n).stream().map(e -> graph.getEdgeSource(e).toString()).collect(Collectors.joining(",")),
						graph.outDegreeOf(n),
						graph.outgoingEdgesOf(n).stream().map(e -> graph.getEdgeTarget(e).toString()).collect(Collectors.joining(","))
			) ) );
	}
}
