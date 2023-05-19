package com.starcases.prime.graph.log;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.core.api.LogPrimeDataIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.impl.PTKLogger;
import com.starcases.prime.graph.impl.PrimeGrapherBase;

import lombok.NonNull;
import picocli.CommandLine.Command;

/**
 * Pulls data from the graph structure and logs it. Main
 * distinctions is the use of the jgrapht api calls to access
 * the main data.
 *
 */
public class LogGraphStructure extends PrimeGrapherBase implements LogPrimeDataIntfc
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(LogGraphStructure.class.getName());

	/**
	 * constructor for logger of graph structure
	 * @param primeSrc
	 * @param baseType
	 */
	public LogGraphStructure(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final BaseTypes baseType)
	{
		super(primeSrc, baseType);
	}

	/**
	 * output log info
	 */
	@Override
	@Command
	public void outputLogs()
	{
		LOG.entering("LogGraphStructure", "l()");
			graph
				.vertexSet()
				.stream()
				.sorted(NODE_COMPARATOR)
				.forEach(n ->
						PTKLogger.output(String.format("Prime %s: created-from:[count(%d), %s] creates-primes:[count(%d), %s]",
						n,
						graph.inDegreeOf(n),
						graph.incomingEdgesOf(n).stream().map(e -> graph.getEdgeSource(e).toString()).collect(Collectors.joining(",")),
						graph.outDegreeOf(n),
						graph.outgoingEdgesOf(n).stream().map(e -> graph.getEdgeTarget(e).toString()).collect(Collectors.joining(","))
			) ) );
	}

	/**
	 * Indicate if use of multiple CPU cores is allowed.
	 */
	@Override
	public LogPrimeDataIntfc doPreferParallel(final boolean preferParallel)
	{
		return this;
	}
}
