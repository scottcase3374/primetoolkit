package com.starcases.prime.graph.export;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.AccessLevel;
import lombok.Getter;
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
	/**
	 * Perform output of GML for the associated element.
	 */
	void output();
}

/**
 * Top-level for GML output, coordinates other class types to
 * generate GML
 */
@SuppressWarnings({"PMD.LawOfDemeter"})
public class ExportGML
{
	/**
	 * default logging
	 */
	private static final Logger LOG = Logger.getLogger(ExportGML.class.getName());

	/**
	 * track elements for GML production - helps with producing
	 *  matching end tags for lists of elements
	 */
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final Deque<ElementIntfc> stack = new ArrayDeque<>();

	/**
	 * Access to prime/primeref lookups.
	 */
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final PrimeSourceIntfc primeSrc;

	/**
	 * Destination for GML output
	 */
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final PrintWriter outputWriter;

	/**
	 * Primary constructor for GML output
	 * @param primeSrc
	 * @param outputWriter
	 */
	public ExportGML(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final PrintWriter outputWriter)
	{
		this.primeSrc = primeSrc;
		this.outputWriter = outputWriter;
	}

	private void graph()
	{
		expGraph();

		final var pRefIter = primeSrc.getPrimeRefIter();

		while (pRefIter.hasNext())
		{
			expNode(pRefIter.next());
		}

		stack.stream().forEach(ElementIntfc::output);
		outputWriter.println("]");
	}

	/**
	 * export of actual data
	 */
	private void expGraph()
	{
		outputWriter.println("graph");
		outputWriter.println("[");
		expDirected();
	}

	private void expDirected()
	{
		outputWriter.println("  directed 1");
	}

	private void expNode(@NonNull final PrimeRefIntfc prime)
	{
		new NodeElement(outputWriter, prime).output();
		stack.add(new EdgeElement(prime, outputWriter));
	}

	/**
	 * Entry point to export data.
	 */
	public void export()
	{
		primeSrc.init();
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("Running Export");
		}
		graph();
	}
}

/**
 * Handles node elements as part of the GML output.
 */
class NodeElement implements ElementIntfc
{
	/**
	 * PrimeRef item to export
	 */
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final PrimeRefIntfc pRef;

	/**
	 * output destination for node GML
	 */
	@Getter(AccessLevel.PRIVATE)
	@NonNull
	private final PrintWriter outputWriter;

	/**
	 * Constructor of nodes which are converted to GML for output
	 * @param outputWriter
	 * @param prime
	 */
	public NodeElement(@NonNull final PrintWriter outputWriter, @NonNull final PrimeRefIntfc prime)
	{
		this.pRef = prime;
		this.outputWriter = outputWriter;
	}

	@Override
	public void output()
	{
		outputWriter.println("  node");
		outputWriter.println("  [");

		outputWriter.println(String.format("    id %d", pRef.getPrime()));
		outputWriter.println(String.format("label \"%s\"", pRef.getPrime()));
		outputWriter.println("  ]");
	}
}

/**
 * Used for exporting edge element information as part of
 * the GML output.
 */
@SuppressWarnings({"PMD.LawOfDemeter"})
class EdgeElement implements ElementIntfc
{
	/**
	 * Edge element data which is output as GML.
	 */
	@NonNull
	@Getter(AccessLevel.PRIVATE)
	private final PrimeRefIntfc pRef;

	/**
	 * GML output destination .
	 */
	@NonNull
	@Getter(AccessLevel.PRIVATE)
	private final PrintWriter outputWriter;

	/**
	 * Constructor representing edges of graph to convert and output as GML
	 * @param prime
	 * @param outputWriter
	 */
	public EdgeElement(@NonNull final PrimeRefIntfc prime, @NonNull final PrintWriter outputWriter)
	{
		this.pRef = prime;
		this.outputWriter = outputWriter;
	}

	@Override
	public void output()
	{
		pRef.getPrimeBaseData().getPrimeBases().get(0).stream().forEach(s -> outputEdge(outputWriter, s, pRef.getPrime()));
	}

	/**
	 * Actual output logic for edges.
	 *
	 * @param outputWriter
	 * @param source
	 * @param target
	 */
	private void outputEdge(@NonNull final PrintWriter outputWriter, @NonNull @Min(1) final BigInteger source, @NonNull @Min(1) final BigInteger target)
	{
		outputWriter.println("  edge");
		outputWriter.println("  [");
		outputWriter.print("    source ");
		outputWriter.println(source);
		outputWriter.print("    target ");
		outputWriter.println(target);
		outputWriter.println("  ]");
	}
}

