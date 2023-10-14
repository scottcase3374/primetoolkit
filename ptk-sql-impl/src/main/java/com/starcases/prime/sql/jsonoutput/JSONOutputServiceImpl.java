package com.starcases.prime.sql.jsonoutput;

import java.util.function.Predicate;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.impl.factory.Lists;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.service.impl.SvcLoader;
import com.starcases.prime.sql.api.OutputServiceIntfc;
import com.starcases.prime.sql.api.PrimeSqlResultIntfc;
import com.starcases.prime.sql.impl.ExclFieldNameStrategy;

public class JSONOutputServiceImpl implements OutputServiceIntfc
{
	private static final Object[] EMPTY_ARRAY = {};
	private static final ImmutableList<BaseTypesIntfc> BASE_TYPES =
			new SvcLoader<BaseTypesProviderIntfc, Class<BaseTypesProviderIntfc>>(BaseTypesProviderIntfc.class)
				.provider( Lists.immutable.of("GLOBAL_BASE_TYPES"))
				.orElseThrow()
				.create();

	private PrimeSourceIntfc primeSrc;
	private PrimeSqlResultIntfc result;

	public JSONOutputServiceImpl()
	{}

	public OutputServiceIntfc init(final PrimeSourceIntfc primeSrc, final PrimeSqlResultIntfc result)
	{
		this.primeSrc = primeSrc;
		this.result = result;
		return this;
	}

	@Override
	public void output(
			final String baseType
			,final long startIdx
			,final boolean useParallel
			,final Predicate<? super PrimeRefIntfc> idxFilter
			,final Predicate<? super ImmutableLongCollection> baseFilter
			)
	{
		final Gson gson = new GsonBuilder().setExclusionStrategies(new ExclFieldNameStrategy()).serializeNulls().create();
		result.setResult(
				gson.toJson(
					primeSrc
					.getPrimeRefStream(startIdx, useParallel)
					// Filter out primes based on index/prime/base-related-info
					.filter(idxFilter)
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
}
