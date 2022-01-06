package com.starcases.prime.graph.log;

import java.util.stream.Collectors;

import com.starcases.prime.graph.impl.PrimeGrapher;
import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

/**
 * Pulls data from the graph structure and logs it. Main 
 * distinctions is the use of the jgrapht api calls to access
 * the main data.
 * 
 */
@Log
public class LogGraphStructure extends PrimeGrapher implements LogGraphIntfc
{
	public LogGraphStructure(@NonNull PrimeSourceIntfc ps, @NonNull BaseTypes baseType)
	{
		super(ps, log, baseType);
	}
	
	@Override
	@Command
	public void log()
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
