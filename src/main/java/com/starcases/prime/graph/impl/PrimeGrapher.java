package com.starcases.prime.graph.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.WindowConstants;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphBuilder;

import com.starcases.prime.graph.visualize.VisualizeGraph;
import com.starcases.prime.intfc.PrimeRefIntfc;
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
public class PrimeGrapher 
{
	private static Comparator<String> nodeComparator = (String o1, String o2) -> Integer.decode(o1).compareTo(Integer.decode(o2));

	private PrimeSourceIntfc ps;
	
	private GraphBuilder<String, DefaultEdge, DefaultDirectedGraph<String, DefaultEdge>> primeGraph = new GraphBuilder<>(new DefaultDirectedGraph<>(DefaultEdge.class));
	
	private Graph<String,DefaultEdge> graph;

	public PrimeGrapher(PrimeSourceIntfc ps)
	{
		this.ps = ps;
		this.ps.init();
		this.populateData();
		this.graph = primeGraph.build();
	}
	
	public void setNodeLocations()
	{
		log.info("setNodeLocations() - start");
		graph
		.vertexSet()
		.stream()
		.sorted(nodeComparator)
		.forEach(n -> 
						{
							float inDegree = graph.inDegreeOf(n);
							float outDegree = graph.outDegreeOf(n);
							Integer id = Integer.decode(n);
							
							float x = 80 - (float)Math.sin(11 * id *127);
							float y = (inDegree * 11 + (float)Math.sin(id)) + (outDegree * 11 + (float)Math.cos(id)) ;
							float z = 0;
						//	n.setX(x);
						//	n.setY(y);
						//	n.setZ(z);
							log.info(String.format("Prime %s  x[%f] y[%f] z[%f]  in-degree[%f] out-degree[%f]", n, x,y,z, inDegree, outDegree ));
						
						});		
		log.info("setNodeLocations() - exit");
}
	
	@Command
	public void logGraphStructure()
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
						graph.outgoingEdgesOf(n).stream().map(e -> graph.getEdgeTarget(e)).collect(Collectors.joining(",")))));		
	}
	
	public void logNodeStructure()
	{
		try
		{
			int i = 0;
			while(true) 
			{ 
				PrimeRefIntfc ref = ps.getPrimeRef(i++);
				System.out.println(String.format("Prime %d bases %s", ref.getPrime(), ref.getIdxPrimes()));
			}
		}
		catch(Exception e)
		{
			log.severe("Exception:" + e);
		}
	}
	
	final BiFunction<Integer, ArrayList<Integer>, Consumer<Integer>> reducer = (m, a)-> idx -> 
	{
		if (idx < m)
		{
			while (m > a.size())
				a.add(0);
			
			a.set(idx, a.get(idx)+1);
		}
		else 
		{ 
			this.primeReduction(idx, this.reducer.apply(m, a)); 
		}
	};
	
	 
	public void logReduced(int maxReduce)
	{
		int i = 0;
		while(true) 
		{ 
			PrimeRefIntfc pr;				
			try
			{
				ArrayList<Integer> ret = new ArrayList<>();
				pr = ps.getPrimeRef(i);
				primeReduction(i++, reducer.apply(maxReduce, ret));
				int [] tmpI = {0};			
				System.out.println(String.format("Prime [%d] %s", pr.getPrime(), 
						ret.stream().map(idx -> String.format("base-%d-count:[%d]", ps.getPrime(tmpI[0]++), idx)).collect(Collectors.joining(", "))));
			}
			catch(Exception e)
			{
				break;
			}				
		}	
	}
	
	private void primeReduction(Integer idx, Consumer<Integer> reducer)
	{	
		ps.getPrimeRef(idx)
		.getPrimeBaseIdxs()
		.forEach(
				bs -> 
					bs
					.stream()
					.boxed()
					.forEach(reducer));
	}
	
	private void populateData()
	{
		// Start setting up the actual graph/data generations
		PrimeNodeGenerator primeNodeGenerator = new PrimeNodeGenerator(ps, primeGraph);
		primeNodeGenerator.begin();
		
		while (primeNodeGenerator.nextEvents());		
	}
	
	/*
	 DOTExporter<URI, DefaultEdge> exporter =
            new DOTExporter<>(v -> v.getHost().replace('.', '_'));
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(hrefGraph, writer);
        System.out.println(writer.toString());
	 */
	
	
	public void viewDefault()
	{
        try
		{
			VisualizeGraph frame = new VisualizeGraph(this.graph);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setSize(400, 320);
			frame.setVisible(true);	
			do
			{
				// will exit when window closes
			} while (System.in.read() != -1);
		}
		catch(Exception e)
		{
			log.severe("Exception:" + e);
		}	        
	}
}
