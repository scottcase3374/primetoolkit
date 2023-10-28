package com.starcases.prime.sql.jsonoutput.impl;

import java.util.function.Predicate;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.impl.factory.Lists;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ExclusionStrategy;
import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.service.impl.SvcLoader;
import com.starcases.prime.sql.api.OutputServiceIntfc;
import com.starcases.prime.sql.api.PrimeSqlResultIntfc;

import lombok.NonNull;

public class JSONOutputSvcImpl implements OutputServiceIntfc
{
	private static final Object[] EMPTY_ARRAY = {};
	private static final ImmutableList<BaseTypesIntfc> BASE_TYPES =
			new SvcLoader<BaseTypesProviderIntfc, Class<BaseTypesProviderIntfc>>(BaseTypesProviderIntfc.class)
				.provider( Lists.immutable.of("GLOBAL_BASE_TYPES"))
				.orElseThrow()
				.create();

	private PrimeSourceIntfc primeSrc;
	private PrimeSqlResultIntfc result;

	public JSONOutputSvcImpl()
	{}

	public OutputServiceIntfc init(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final PrimeSqlResultIntfc result)
	{
		this.primeSrc = primeSrc;
		this.result = result;
		return this;
	}

	@Override
	public void output(
			final String baseType
			,final long startIdx
			,final long maxIndexes
			,final boolean useParallel
			,@NonNull final Predicate<? super PrimeRefIntfc> idxFilter
			,@NonNull final Predicate<? super ImmutableLongCollection> baseFilter
			,@NonNull final Object excludeStrategyFilter
			)
	{
		try
		{
			final ExclusionStrategy excludeStrategy = (ExclusionStrategy)excludeStrategyFilter;
			final Gson gson = new GsonBuilder().setExclusionStrategies(excludeStrategy).serializeNulls().create();
			result.setResult(
					gson.toJson(
						primeSrc
						.getPrimeRefStream(startIdx, useParallel)
						.limit(maxIndexes)
						.<JsonData>map(pRef -> new JsonData(
								pRef.getPrimeRefIdx(),
								pRef.getPrime(),
								baseType != null
									? pRef.getPrimeBaseData()
										.getPrimeBases(BASE_TYPES.select(base -> base.name().equals(baseType)).getOnly())
										.stream()
										// Filter tuples out of bases for each matched prime which where tuple doesn't meet the match criteria
										.filter(baseFilter)
										.map(lc -> lc.toArray())
										.toArray()
									: EMPTY_ARRAY))
						.toArray()));
		}
		catch(final Exception e)
		{
			System.out.println("*** Json output exception " + e.toString());
			e.printStackTrace();
		}
	}
}
