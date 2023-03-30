package com.starcases.prime.antlr;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.starcases.prime.antlrimpl.PrimeSqlBaseVisitor;
import com.starcases.prime.antlrimpl.PrimeSqlParser;
import com.starcases.prime.antlrimpl.PrimeSqlParser.ArrayContext;
import com.starcases.prime.antlrimpl.PrimeSqlParser.GreaterEqualThanContext;
import com.starcases.prime.antlrimpl.PrimeSqlParser.GreaterThanContext;
import com.starcases.prime.antlrimpl.PrimeSqlParser.LessEqualThanContext;
import com.starcases.prime.antlrimpl.PrimeSqlParser.LessThanContext;
import com.starcases.prime.antlrimpl.PrimeSqlParser.MatchArrayContext;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

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
	private static final Object [] EMPTY_ARRAY = {};

	@Getter(AccessLevel.PRIVATE)
	private static final Logger LOG = Logger.getLogger(PrimeSqlVisitor.class.getName());

    private final PrimeSourceIntfc primeSrc;
	private final PrimeSqlResult result = new PrimeSqlResult();
	private final List<Predicate<PrimeRefIntfc>> predList = new ArrayList<>();
	private final ExclFieldNameStrategy excludePrime = new ExclFieldNameStrategy("prime");
	private final ExclFieldNameStrategy excludeBases = new ExclFieldNameStrategy("bases");
	private final ExclFieldNameStrategy excludeNothing = new ExclFieldNameStrategy("<NOTHING>");

	private ExclFieldNameStrategy exclude = excludeNothing;
	private String baseType;

	/**
	 * Constructor for the visitor type; the PrimeSourceIntfc provides
	 * access to the set of primes needed to perform search/filter/etc
	 * operations.
	 *
	 * @param primeSrc
	 */
	public PrimeSqlVisitor(final PrimeSourceIntfc primeSrc)
	{
		super();
		this.primeSrc = primeSrc;
	}

	  class ExclFieldNameStrategy implements ExclusionStrategy
	  {
		  private final String fieldName;

		  ExclFieldNameStrategy(final String fieldName)
		  {
		    this.fieldName = fieldName;
		  }

		  @Override
		  public boolean shouldSkipClass(Class<?> clazz)
		  {
		    return false;
		  }

		  @Override
		  public boolean shouldSkipField(FieldAttributes f)
		  {
		    return f.getName().equals(fieldName);
		  }
		}

	interface RetBase {}

	interface RetBases extends RetBase
	{
		Object [] getBases();
	}

	interface RetPrime extends RetBase
	{
		long getPrime();
	}

	class RetData implements RetPrime, RetBases
	{
		@Setter
		private long prime;

		@Setter
		private Object [] bases;

		public RetData(final long prime, final Object [] bases)
		{
			this.prime = prime;
			this.bases = bases;
		}

		public RetData()
		{}

		@Override
		public long getPrime()
		{
			return prime;
		}

		@Override
		public Object [] getBases()
		{
			return bases;
		}
	}

	/**
	 * This method overrides the default and applies filters for the prime set
	 * based upon the parsed data.
	 */
	@Override
	public PrimeSqlResult visitRoot(final PrimeSqlParser.RootContext ctx)
	{
		visitChildren(ctx);

		final Gson gson = new GsonBuilder()
				.setExclusionStrategies(exclude)
				.serializeNulls()
				.create();

		final MutableList<Stream<PrimeRefIntfc>> curStream = Lists.mutable.empty();
		curStream.add(primeSrc.getPrimeRefStream(false));

		// Assign filter for primes and any general filter for bases
		predList.forEach(pr -> curStream.set(0, curStream.get(0).filter(pr)));

		try
		{

		// This is the "manual" way of converting to json. Some custom serialization
		// support would be better (i.e. less garbage collection needed for temp arrays).
		result.setResult(gson.toJson(
			curStream
			.get(0)
			.map(pr ->
					new RetData(
							pr.getPrime(),
							baseType != null ?
									pr.getPrimeBaseData()
										.getPrimeBases(BaseTypes.valueOf(baseType))
										.stream()
										.map(lc -> lc.toArray())
										.toArray()
									: EMPTY_ARRAY
								)
				).toArray()
				)
			);

		} catch (
				 	IllegalArgumentException
					| SecurityException e)
		{

			LOG.severe(e.toString());
		}

		return result;
	}

	@Override
	public PrimeSqlResult visitSelect_scope(PrimeSqlParser.Select_scopeContext ctx)
	{
		switch(ctx.getChild(0).getText().toUpperCase(Locale.ENGLISH))
		{
			case "*":
				exclude = excludeNothing;
				break;

			case "PRIMES":
				exclude = excludeBases;
				break;

			case "BASES":
				exclude = excludePrime;
		}
		return visitChildren(ctx);
	}

	/**
	 * Parse data and create a predicate appropriate for the parsed data.
	 * The predicate is saved for later processing.
	 */
	@Override
	public PrimeSqlResult visitGreaterThan(final GreaterThanContext ctx)
	{
		this.visitChildren(ctx);

		final var limitIdxTxt = ctx.getChild(1).getText();
		final var limitIdx = Long.valueOf(limitIdxTxt);

		final var limitPri = primeSrc.getPrimeForIdx(limitIdx).getAsLong();
		predList.add(p -> p.getPrime() > limitPri);

		return result;
	}

	/**
	 * Parse data and create a predicate appropriate for the parsed data.
	 * The predicate is saved for later processing.
	 */
	@Override
	public PrimeSqlResult visitLessThan(final LessThanContext ctx)
	{
		this.visitChildren(ctx);

		final var limitIdxTxt = ctx.getChild(1).getText();
		final var limitIdx = Long.valueOf(limitIdxTxt);

		final var limitPri = primeSrc.getPrimeForIdx(limitIdx).getAsLong();
		predList.add(p -> p.getPrime() < limitPri);

		return result;
	}

	/**
	 * Parse data and create a predicate appropriate for the parsed data.
	 * The predicate is saved for later processing.
	 */
	@Override
	public PrimeSqlResult visitGreaterEqualThan(final GreaterEqualThanContext ctx)
	{
		this.visitChildren(ctx);

		final var limitIdxTxt = ctx.getChild(1).getText();
		final var limitIdx = Long.valueOf(limitIdxTxt);

		final var limitPri = primeSrc.getPrimeForIdx(limitIdx).getAsLong();
		predList.add(p -> p.getPrime() >= limitPri);
		return result;
	}

	/**
	 * Parse data and create a predicate appropriate for the parsed data.
	 * The predicate is saved for later processing.
	 */
	@Override
	public PrimeSqlResult visitLessEqualThan(final LessEqualThanContext ctx)
	{
		this.visitChildren(ctx);

		final var limitIdxTxt = ctx.getChild(1).getText();
		final var limitIdx = Long.valueOf(limitIdxTxt);

		final var limitPri = primeSrc.getPrimeForIdx(limitIdx).getAsLong();
		predList.add(p -> p.getPrime() <= limitPri);

		return result;
	}

	/**
	 * Parse data and create a predicate appropriate for the parsed data.
	 * The predicate is saved for later processing.
	 */
	@Override
	public PrimeSqlResult visitArray(final ArrayContext ctx)
	{
		this.visitChildren(ctx);

		final var childCnt = ctx.getChildCount();
		final var baseCnt = Math.floorDiv(childCnt,2);

		final long [] vals = new long[baseCnt];

		for (int i=0; i<baseCnt ; i++)
		{
			final var primeTxt = ctx.getChild(i*2+1).getText();
			vals[i] = Long.valueOf(primeTxt);
		}

		predList.add(
				p ->
					p.getPrimeBaseData()
					 .getPrimeBases(BaseTypes.valueOf(baseType.toUpperCase(Locale.ENGLISH)))
					 .parallelStream()
					 .anyMatch(c -> c.containsAll(vals)));
		return result;
	}

	/**
	 * Parse data and create a predicate appropriate for the parsed data.
	 * The predicate is saved for later processing.
	 */
	@Override
	public PrimeSqlResult visitMatchArray(final MatchArrayContext ctx)
	{
		this.visitChildren(ctx);

		baseType = ctx.getChild(1).getText();
		return result;
	}
}
