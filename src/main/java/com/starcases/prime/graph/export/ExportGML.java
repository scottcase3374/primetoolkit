package com.starcases.prime.graph.export;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import lombok.extern.java.Log;
import lombok.NonNull;
import javax.validation.constraints.Min;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

/**
 * Quick and dirty export
 *
 */

interface ElementIntfc
{
	void output();
}

@Log
public class ExportGML
{
	@NonNull
	final Deque<ElementIntfc> stack = new ArrayDeque<>();

	@NonNull
	PrimeSourceIntfc ps;

	@NonNull
	PrintWriter pr;

	public ExportGML(@NonNull PrimeSourceIntfc ps, @NonNull PrintWriter pr)
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

	private void expNode(@NonNull PrimeRefIntfc prime)
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
	@NonNull
	PrimeRefIntfc pRef;

	@NonNull
	PrimeSourceIntfc ps;

	@NonNull
	PrintWriter pr;

	public NodeElement(@NonNull PrimeSourceIntfc ps, @NonNull PrintWriter pr, @NonNull PrimeRefIntfc prime)
	{
		this.pRef = prime;
		this.ps = ps;
		this.pr = pr;
	}

	public void output()
	{
		pr.println("  node");
		pr.println("  [");

		pr.println(String.format("    id %d", pRef.getPrime()));
		pr.println(String.format("label \"%s\"", pRef.getPrime()));
		pr.println("  ]");
	}
}

class EdgeElement implements ElementIntfc
{
	@NonNull
	PrimeRefIntfc pRef;

	@NonNull
	PrintWriter pr;

	public EdgeElement(@NonNull PrimeRefIntfc prime, @NonNull PrintWriter pr)
	{
		this.pRef = prime;
		this.pr = pr;
	}

	public void output()
	{
		pRef.getPrimeBaseData().getPrimeBaseIdxs().get(0).stream().forEach(s -> outputEdge(pr, s, pRef.getPrime()));
	}

	private void outputEdge(@NonNull PrintWriter pr, @NonNull @Min(1) Integer source, @NonNull @Min(1) BigInteger target)
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

