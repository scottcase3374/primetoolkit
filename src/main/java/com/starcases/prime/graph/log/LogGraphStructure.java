package com.starcases.prime.graph.log;

import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphBuilder;

import com.starcases.prime.graph.impl.PrimeGrapher;
import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

// 
// start 1
//       2
//       3 <-  2 + 1
//       5 <-  3 + 2
//       7 <-  5 + 2
//       11 <- 7+3+1; 5+3+2+1
@Log
public class LogGraphStructure extends PrimeGrapher implements LogGraphIntfc
{
	public LogGraphStructure(PrimeSourceIntfc ps)
	{
		super(ps, log);
	}
	
	@Override
	@Command
	public void log()
	{
		System.out.println("log structure");
			graph
				.vertexSet()				
				.stream()
				.sorted(nodeComparator)
				.forEach(n -> 
							System.out.println(String.format("Prime %s: created-from:[count(%d), %s] creates-primes:[count(%d), %s]", 
						n, 
						graph.inDegreeOf(n),
						graph.incomingEdgesOf(n).stream().map(e -> graph.getEdgeSource(e)).collect(Collectors.joining(",")),
						graph.outDegreeOf(n),
						graph.outgoingEdgesOf(n).stream().map(e -> graph.getEdgeTarget(e)).collect(Collectors.joining(","))
			) ) );		
	}
}
