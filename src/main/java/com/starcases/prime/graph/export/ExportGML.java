package com.starcases.prime.graph.export;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import lombok.extern.java.Log;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

/**
 * Quick and dirty export - no attempt at optimizing.
 *
 */

interface ElementIntfc
{
	void output();
}

@Log
public class ExportGML 
{
	Deque<ElementIntfc> stack = new ArrayDeque<>();
	
	PrimeSourceIntfc ps;
	PrintWriter pr;
	
	public ExportGML(PrimeSourceIntfc ps, PrintWriter pr)
	{
		this.ps = ps;
		this.pr = pr;
	}
	
	private void graph()
	{	
		expGraph();
		
		try
		{			
			for (var i=0; i < ps.getMaxIdx(); i++)
				expNode(ps.getPrimeRef(i).orElseThrow());
			
			stack.stream().forEach(ElementIntfc::output);
			
			pr.println("]");
		}
		catch(Exception e)
		{
			log.severe("exception in graph: " + e);
		}
	}
	
	private void expGraph()
	{
		pr.println("graph");
		pr.println("[");
		expDirected();
	}
	
	private void expDirected()
	{
		pr.println("  directed 1");
	}
	
	private void expNode(PrimeRefIntfc prime)
	{
		new NodeElement(ps, pr, prime).output();
		stack.add(new EdgeElement(prime, pr));
	}
	
	public void export()
	{
		ps.init();
		graph();
	}
}

class NodeElement implements ElementIntfc
{
	PrimeRefIntfc pRef;
	PrimeSourceIntfc ps;
	PrintWriter pr;
	
	public NodeElement(PrimeSourceIntfc ps, PrintWriter pr, PrimeRefIntfc prime)
	{
		this.pRef = prime;
		this.ps = ps;
		this.pr = pr;
	}
	
	public void output()
	{
		pr.println("  node");
		pr.println("  [");
		
		pr.println(String.format("    id %d\n    label \"%s\"", pRef.getPrime(), pRef.getPrime()));
		pr.println("  ]");
	}	
}

class EdgeElement implements ElementIntfc
{
	PrimeRefIntfc pRef;
	PrintWriter pr;
	
	public EdgeElement(PrimeRefIntfc prime, PrintWriter pr)
	{
		this.pRef = prime;
		this.pr = pr;
	}
	
	public void output()
	{
		pRef.getPrimeBaseIdxs().stream().forEach(s -> outputEdge(pr, s, pRef.getPrime()));
	}
	
	private void outputEdge(PrintWriter pr, Integer source, BigInteger target)
	{
		pr.println("  edge");
		pr.println("  [");
		pr.print("    source ");
		pr.println(source);
		pr.print("    target ");
		pr.println(target);
		pr.println("  ]");
	}
}

