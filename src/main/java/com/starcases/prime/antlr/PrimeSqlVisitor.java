package com.starcases.prime.antlr;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.factory.Stacks;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.collector.Collectors2;

import com.starcases.prime.antlrimpl.PrimeSqlBaseVisitor;
import com.starcases.prime.antlrimpl.PrimeSqlParser;
import com.starcases.prime.antlrimpl.PrimeSqlParser.GreaterThanContext;
import com.starcases.prime.antlrimpl.PrimeSqlParser.LessThanContext;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

/**
 * Visit the parse tree nodes, gather values needed for the query and add/apply the
 * correct predicates/operations to the prime collection.
 *
 * NOTE: There is likely some further cleanup that can be done here. I would also
 * like to update it to improve the ability to add new operations to the
 * pipeline of stream operations.
 *
 * @author scott
 *
 */
public class PrimeSqlVisitor extends PrimeSqlBaseVisitor<PrimeSqlResult>
{
    private final  PrimeSourceIntfc primeSrc;
	private final PrimeSqlResult result = new PrimeSqlResult();
	private final List<Predicate<PrimeRefIntfc>> predList = new ArrayList<>();

	/**
	 * Constructor for the visitor type; the PrimeSourceIntfc provides
	 * access to the set of primes needed to perform search/filter/etc
	 * operations.
	 *
	 * @param primeSrc
	 */
	public PrimeSqlVisitor(final PrimeSourceIntfc primeSrc)
	{
		this.primeSrc = primeSrc;
	}

	/**
	 * I believe this can be done differently but I am leaving as is for now while I determine
	 * what other types of operations might be worth handling.
	 */
	protected PrimeSqlResult aggregateResult(PrimeSqlResult aggregate, PrimeSqlResult nextResult)
	{
		PrimeSqlResult res = nextResult;
		if (res != null)
		{
			res.setResult(nextResult.getResult());
		}
		else
		{
			res = aggregate;
		}
		return res;
	}

	/**
	 * This method overrides the default and applies filters for the prime set
	 * based upon the parsed data.
	 */
	@Override
	public PrimeSqlResult visitRoot(PrimeSqlParser.RootContext ctx)
	{
		visitChildren(ctx);
		final MutableStack<Stream<PrimeRefIntfc>> ps = Stacks.mutable.empty();
		ps.push(primeSrc.getPrimeRefStream(false));
		predList.forEach(pr -> ps.push(ps.getFirst().filter(pr)));
		result.setResult(ps.getFirst().collect(Collectors2.makeString()));
		return result;
	}

	/**
	 * Parse data and create a predicate appropriate for the parsed data.
	 * The predicate is saved for later processing.
	 */
	@Override
	public PrimeSqlResult visitGreaterThan(GreaterThanContext ctx)
	{
		final var limitTxt = ctx.getChild(1).getText();
		final var limit = Long.valueOf(limitTxt);
		this.visitChildren(ctx);

		final var limitPri = primeSrc.getPrimeForIdx(limit).getAsLong();

		predList.add(p -> p.getPrime() > limitPri);

		this.visitChildren(ctx);
		return result;
	}

	/**
	 * Parse data and create a predicate appropriate for the parsed data.
	 * The predicate is saved for later processing.
	 */
	@Override
	public PrimeSqlResult visitLessThan(LessThanContext ctx)
	{
		final var limitTxt = ctx.getChild(1).getText();
		final var limit = Long.valueOf(limitTxt);
		this.visitChildren(ctx);

		final var limitPri = primeSrc.getPrimeForIdx(limit).getAsLong();

		predList.add(p -> p.getPrime() < limitPri);

		return result;
	}
}
