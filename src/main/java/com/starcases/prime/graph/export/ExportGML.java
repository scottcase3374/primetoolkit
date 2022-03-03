package com.starcases.prime.graph.export;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;

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

public class ExportGML
{
	private static final Logger log = Logger.getLogger(ExportGML.class.getName());

	@NonNull
	private final Deque<ElementIntfc> stack = new ArrayDeque<>();

	@NonNull
	private final PrimeSourceIntfc ps;

	@NonNull
	private final PrintWriter pr;

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
			var it = ps.getPrimeRefIter();

			while (it.hasNext())
				expNode(it.next());

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
	private final PrimeRefIntfc pRef;

	@NonNull
	private final PrimeSourceIntfc ps;

	@NonNull
	private final PrintWriter pr;

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
	private final PrimeRefIntfc pRef;

	@NonNull
	private final PrintWriter pr;

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

