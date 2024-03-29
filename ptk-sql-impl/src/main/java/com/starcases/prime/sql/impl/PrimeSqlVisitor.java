package com.starcases.prime.sql.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.primitive.LongPredicate;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.service.impl.SvcLoader;
import com.starcases.prime.sql.antlrimpl.PrimeSqlBaseVisitor;
import com.starcases.prime.sql.antlrimpl.PrimeSqlParser;
import com.starcases.prime.sql.antlrimpl.PrimeSqlParser.ArrayItemContext;
import com.starcases.prime.sql.antlrimpl.PrimeSqlParser.Array_top_clauseContext;
import com.starcases.prime.sql.antlrimpl.PrimeSqlParser.BaseMatchContext;
import com.starcases.prime.sql.antlrimpl.PrimeSqlParser.Idx_boundsContext;
import com.starcases.prime.sql.antlrimpl.PrimeSqlParser.InsertContext;
import com.starcases.prime.sql.antlrimpl.PrimeSqlParser.Sel_optsContext;
import com.starcases.prime.sql.antlrimpl.PrimeSqlParser.SubArrayContext;
import com.starcases.prime.sql.api.OutputProviderIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * Visit the parse tree nodes, gather values needed for the query and add/apply
 * the correct predicates/operations to the prime collection.
 *
 * NOTE: There is likely some further cleanup that can be done here. I would
 * also like to update it to improve the ability to add new operations to the
 * pipeline of stream operations.
 *
 * @author scott
 *
 */
class PrimeSqlVisitor extends PrimeSqlBaseVisitor<PrimeSqlResult>
{
	@Getter(AccessLevel.PRIVATE)
	private static final Logger LOG = Logger.getLogger(PrimeSqlVisitor.class.getName());

	private static final ImmutableList<BaseTypesIntfc> BASE_TYPES =
			new SvcLoader<BaseTypesProviderIntfc, Class<BaseTypesProviderIntfc>>(BaseTypesProviderIntfc.class)
				.provider( Lists.immutable.of("GLOBAL_BASE_TYPES"))
				.orElseThrow()
				.create();

	private final String contentType;

	private final PrimeSourceIntfc primeSrc;

	@Getter
	private final PrimeSqlResult result = new PrimeSqlResult();

	/**
	 * Filter returned primes by index/prime-val/bases
	 */
	private final MutableCollection<Predicates<PrimeRefIntfc>> primePredColl = Lists.mutable.empty();

	/**
	 * Filter base tuples returned
	 */
	private final MutableCollection<LongPredicate> primeBaseItemPredColl = Lists.mutable.empty();
	private final MutableCollection<Predicates<ImmutableLongCollection>> primeBaseTuplePredColl = Lists.mutable.empty();

	private LongArrayList anyItemsColl = new LongArrayList();
	private MutableCollection<long[]> itemGroupColl = Lists.mutable.empty();


	private static final String FIELD_INDEX = "index";
	private static final String FIELD_PRIME = "prime";
	private static final String FIELD_BASE = "base";
	private static final String FIELD_SPLAT = "*";
	private static final String FIELD_EXCLUDE_NONE = FIELD_SPLAT;

	private ImmutableList<String> fieldExclusionFields;

	private boolean selUseParallel;

	private String baseType;

	private long greaterThanAttr = -1;
	private long maxIndexCount = 0;

	/**
	 * Constructor for the visitor type; the PrimeSourceIntfc provides access to the
	 * set of primes needed to perform search/filter/etc operations.
	 *
	 * @param primeSrc
	 */
	public PrimeSqlVisitor(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final String contentType)
	{
		super();
		this.primeSrc = primeSrc;
		this.contentType = contentType;
	}


	/**
	 * Class defining possible data values to return to caller of the SQL-like
	 * processor. The specific fields returned are filtered based upon the query
	 * received.
	 *
	 * @author scott
	 *
	 */


	@Override
	public PrimeSqlResult visitRoot(final PrimeSqlParser.RootContext ctx)
	{
		visitChildren(ctx);
		return result;
	}

	@Override
	public PrimeSqlResult visitStmts(final PrimeSqlParser.StmtsContext ctx)
	{
		visitChildren(ctx);
		return result;
	}

	@Override
	public PrimeSqlResult visitStmt(final PrimeSqlParser.StmtContext ctx)
	{
		visitChildren(ctx);
		return result;
	}

	@Override
	public PrimeSqlResult visitShow_plugins(final PrimeSqlParser.Show_pluginsContext ctx)
	{
		visitChildren(ctx);
		return result;
	}

	@Override
	public PrimeSqlResult visitSelect(final PrimeSqlParser.SelectContext ctx)
	{
		visitChildren(ctx);
		final Predicate<? super PrimeRefIntfc> idxFilter =
				 pRef -> primePredColl.stream().allMatch(primeFilt -> primeFilt.accept(pRef));

		final Predicate<? super ImmutableLongCollection> baseFilter =
				 baseColl ->
					   primeBaseItemPredColl.stream().anyMatch(baseColl::anySatisfy)
					|| primeBaseTuplePredColl.stream().anyMatch(tupleFilt -> tupleFilt.accept(baseColl));
		try
		{
			 new SvcLoader<OutputProviderIntfc, Class<OutputProviderIntfc>>(OutputProviderIntfc.class)
			 	.provider( Lists.immutable.of(contentType.toUpperCase()))
			 	.orElseThrow()
			 	.create(primeSrc, result)
			 	.output(baseType, greaterThanAttr, this.maxIndexCount, selUseParallel, idxFilter, baseFilter, fieldExclusionFields);
		}
		catch (final Exception e)
		{
			final Gson gson = new GsonBuilder().serializeNulls().create();
			final StringWriter strWriter = new StringWriter();
			final PrintWriter prtWriter = new PrintWriter(strWriter);
			e.printStackTrace(prtWriter);

			result.setError(gson.toJson(strWriter.getBuffer().toString()));

			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe(e.getMessage());
			}
		}

		return result;
	}

	@Override
	public PrimeSqlResult visitSelect_field(final PrimeSqlParser.Select_fieldContext ctx)
	{
		visitChildren(ctx);
		final MutableList<String> excludes = Lists.mutable.empty();

		switch (ctx.sel.getType())
		{
			case PrimeSqlParser.PRIMES:
				excludes.add(FIELD_BASE);
				if (ctx.idx_sel == null || ctx.idx_sel.getType() != PrimeSqlParser.INDEX)
				{
					excludes.add(FIELD_INDEX);
				}
				break;

			case PrimeSqlParser.BASES:
				excludes.add(FIELD_PRIME);
				if (ctx.idx_sel == null || ctx.idx_sel.getType() != PrimeSqlParser.INDEX)
				{
					excludes.add(FIELD_INDEX);
				}
				break;

			case PrimeSqlParser.SPLAT:
			default:
				if (ctx.idx_sel != null && ctx.idx_sel.getType() == PrimeSqlParser.NO)
				{
					excludes.add(FIELD_INDEX);
				}
				else
				{
					excludes.add(FIELD_EXCLUDE_NONE);
				}
				break;
		}

		fieldExclusionFields = excludes.toImmutable();
		return result;
	}

	@Override
	public PrimeSqlResult visitSel_opts(final Sel_optsContext ctx)
	{
		this.visitChildren(ctx);
		this.selUseParallel = true;
		return result;
	}

	/**
	 * Parse data and create a predicate appropriate for the parsed data. The
	 * predicate is saved for later processing.
	 */
	@Override
	public PrimeSqlResult visitIdx_bounds(final Idx_boundsContext ctx)
	{
		this.visitChildren(ctx);

		Predicates<PrimeRefIntfc> pred = null;

		if (ctx.opG != null)
		{
			final long great = Long.parseLong(ctx.gval.getText());
			greaterThanAttr = great;
			switch(ctx.opG.getType())
			{
				case PrimeSqlParser.GT:
					pred = Predicates.attributeGreaterThan(PrimeRefIntfc::getPrimeRefIdx, great);
					break;

				case PrimeSqlParser.GT_EQUAL:
					pred = Predicates.attributeGreaterThanOrEqualTo(PrimeRefIntfc::getPrimeRefIdx, great);
					break;

				default:
					pred = Predicates.attributeGreaterThanOrEqualTo(PrimeRefIntfc::getPrimeRefIdx, 0L);
			}
		}

		if (ctx.opL != null)
		{
			final long less = Long.parseLong(ctx.lval.getText());
			this.maxIndexCount = Math.max(0, less - greaterThanAttr);

			final Predicates<PrimeRefIntfc> pred2;
			switch(ctx.opL.getType())
			{
				case PrimeSqlParser.LT:
					pred2 = Predicates.attributeLessThan(PrimeRefIntfc::getPrimeRefIdx, less);
					break;

				case PrimeSqlParser.LT_EQUAL:
					pred2 = Predicates.attributeLessThanOrEqualTo(PrimeRefIntfc::getPrimeRefIdx, less);
					break;

				default:
					pred2 = null;
			}

			if (pred != null)
			{
				if (pred2 != null)
				{
					pred = pred.and(pred2);
				}
			}
			else
			{
				pred = pred2;
			}
		}

		if (pred != null)
		{
			primePredColl.add(pred);
		}

		return result;
	}

	/**
	 * Parse data and create a predicate appropriate for the parsed data. The
	 * predicate is saved for later processing.
	 */
	@Override
	public PrimeSqlResult visitBaseMatch(final BaseMatchContext ctx)
	{
		this.visitChildren(ctx);

		baseType = ctx.getChild(1).getText();
		return result;
	}

	@Override
	public PrimeSqlResult visitSubArray(final SubArrayContext ctx)
	{
		visitChildren(ctx);
		final var childCnt = ctx.getChildCount();
		final var baseCnt = Math.floorDiv(childCnt, 2);
		final long[] items = new long[baseCnt];

		for (int i = 0; i < baseCnt; i++)
		{
			final var primeTxt = ctx.getChild(i * 2 + 1).getText();
			items[i] = Long.parseLong(primeTxt);
		}
		itemGroupColl.add(items);

		return result;
	}

	@Override
	public PrimeSqlResult visitArrayItem(final ArrayItemContext ctx)
	{
		visitChildren(ctx);
		final String primeTxt = ctx.getChild(0).getText();
		anyItemsColl.add(Long.parseLong(primeTxt));

		return result;
	}

	/**
	 * Parse data and create a predicate appropriate for the parsed data. The
	 * predicate is saved for later processing.
	 */
	@Override
	public PrimeSqlResult visitArray_top_clause(final Array_top_clauseContext ctx)
	{
		this.visitChildren(ctx);

		// Predicate testing each prime's base tuples for any single item from a collection of items.
		// The Prime is returned if any base tuple has at least one item out of the collection.
		if (!anyItemsColl.isEmpty())
		{
	 		primePredColl.add( Predicates.adapt(
	 			pRef -> pRef.getPrimeBaseData()
	 						.getPrimeBases(BASE_TYPES.select(base -> base.name().equals(baseType.toUpperCase(Locale.ENGLISH))).getOnly())
	 						.parallelStream()
	 						.anyMatch(baseColl -> baseColl.containsAny(anyItemsColl)))
				  );
		}
//		// Predicate testing each prime's base tuples for membership of a group of primes in a tuple.
//		// The Prime is returned if any base tuple contains at least one group of primes
// 		// from the collection of groups.
 		if (!itemGroupColl.isEmpty())
 		{
 			primePredColl.add( Predicates.adapt(
 					pRef -> pRef.getPrimeBaseData()
 								.getPrimeBases(BASE_TYPES.select(base -> base.name().equals(baseType.toUpperCase(Locale.ENGLISH))).getOnly())
 								.parallelStream()
 								.anyMatch(baseColl -> itemGroupColl
 														.stream()
 														.anyMatch(baseColl::containsAll)))
 						);
 		}

		// Predicate testing each prime's base tuples for any single item from a collection of items.
		// The tuple is returned if the tuple has at least one item out of the collection.
		if (!anyItemsColl.isEmpty())
		{
	 		primeBaseItemPredColl.add(baseItem ->  anyItemsColl.contains(baseItem));
		}
		// Predicate testing each prime's base tuples for membership of a group of primes in a tuple.
		// The tuple is returned if the tuple contains at least one group of primes
 		// from the collection of groups.
 		if (!itemGroupColl.isEmpty())
 		{ ///
 			primeBaseTuplePredColl.add( Predicates.adapt(
 					baseItem ->   itemGroupColl
 									.stream()
 									.anyMatch(baseItem::containsAll)));
 		}

		return result;
	}


	@Override
	public PrimeSqlResult visitInsert(InsertContext ctx)
	{
//		final long numCacheEntry = cache.getNumCacheEntries();
//		SubsetIntfc<Long> subset = cache.get(numCacheEntry-1);
//
//		boolean persist = false;
//
//		if (subset.getMaxOffsetAssigned() >= subset.getMaxOffset()-1)
//		{
//			final PrimeSubsetProviderIntfc subsetProvider = new SvcLoader<PrimeSubsetProviderIntfc, Class<PrimeSubsetProviderIntfc>>(PrimeSubsetProviderIntfc.class)
//					.provider(Lists.immutable.of("PRIMESUBSET")).orElseThrow();
//
//			subset = subsetProvider.create((int)numCacheEntry, new long[subset.getMaxOffset()]);
//			persist = true;
//		}
//
//		subset.set(subset.getMaxOffsetAssigned() % subset.getMaxOffset(), Long.valueOf(ctx.val.getText()));
//
//		if (persist)
//		{
//			cache.persist(numCacheEntry, subset);
//		}
		return result;
	}
}
