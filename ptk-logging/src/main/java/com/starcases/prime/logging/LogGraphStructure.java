package com.starcases.prime.logging;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.core.api.LogPrimeDataIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.graph.impl.PrimeGrapherBase;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.NonNull;

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

	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
	/**
	 * constructor for logger of graph structure
	 * @param primeSrc
	 * @param baseType
	 */
	public LogGraphStructure(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final BaseTypesIntfc baseType)
	{
		super(primeSrc, baseType);
	}

	/**
	 * output log info
	 */
	@Override
	public void outputLogs()
	{
		LOG.entering("LogGraphStructure", "l()");
			graph
				.vertexSet()
				.stream()
				.sorted(NODE_COMPARATOR)
				.forEach(n ->
						statusHandler.output(String.format("Prime %s: created-from:[count(%d), %s] creates-primes:[count(%d), %s]",
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
