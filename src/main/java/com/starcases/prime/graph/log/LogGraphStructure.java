package com.starcases.prime.graph.log;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.graph.impl.AbstractPrimeGrapher;
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
public class LogGraphStructure extends AbstractPrimeGrapher implements LogGraphIntfc
{
	private static final Logger LOG = Logger.getLogger(LogGraphStructure.class.getName());

	public LogGraphStructure(@NonNull final PrimeSourceIntfc ps, @NonNull final BaseTypes baseType)
	{
		super(ps, baseType);
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	@Command
	public void l()
	{
		LOG.entering("LogGraphStructure", "l()");
			graph
				.vertexSet()
				.stream()
				.sorted(NODE_COMPARATOR)
				.forEach(n ->
							PrimeToolKit.output(String.format("Prime %s: created-from:[count(%d), %s] creates-primes:[count(%d), %s]",
						n,
						graph.inDegreeOf(n),
						graph.incomingEdgesOf(n).stream().map(e -> graph.getEdgeSource(e).toString()).collect(Collectors.joining(",")),
						graph.outDegreeOf(n),
						graph.outgoingEdgesOf(n).stream().map(e -> graph.getEdgeTarget(e).toString()).collect(Collectors.joining(","))
			) ) );
	}

	@Override
	public LogGraphIntfc doPreferParallel(final boolean preferParallel)
	{
		return this;
	}
}
