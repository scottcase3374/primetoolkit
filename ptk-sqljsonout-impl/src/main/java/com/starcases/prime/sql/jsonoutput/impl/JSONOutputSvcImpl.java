package com.starcases.prime.sql.jsonoutput.impl;

import java.util.Arrays;
import java.util.function.Predicate;

import org.eclipse.collections.api.block.predicate.primitive.LongPredicate;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.api.list.ImmutableList;
//import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.impl.factory.Lists;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.service.impl.SvcLoader;
import com.starcases.prime.sql.api.OutputServiceIntfc;
import com.starcases.prime.sql.api.PrimeResultIntfc;

import lombok.NonNull;

public class JSONOutputSvcImpl implements OutputServiceIntfc
{
	private static final long[] EMPTY_ARRAY = {};
	private static final ImmutableList<BaseTypesIntfc> BASE_TYPES =
			new SvcLoader<BaseTypesProviderIntfc, Class<BaseTypesProviderIntfc>>(BaseTypesProviderIntfc.class)
				.provider( Lists.immutable.of("GLOBAL_BASE_TYPES"))
				.orElseThrow()
				.create();

	private PrimeSourceIntfc primeSrc;
	private PrimeResultIntfc result;

	public JSONOutputSvcImpl()
	{ /* nothing to do */ }

	public OutputServiceIntfc init(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final PrimeResultIntfc result)
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
			,@NonNull final LongPredicate anyBasePred
			,@NonNull final Predicate<? super ImmutableLongCollection> entireBasePred 
			,@NonNull final ImmutableList<String> excludeFields
			)
	{
		try
		{

			final ExclFieldNameStrategy excludes = new ExclFieldNameStrategy();
			excludeFields.forEach(excludes::addExcludedField);
			System.out.printf("JSON Output - basetype[%s] startIdx[%d] \n", baseType, startIdx);
			System.out.printf("JSON output - first prime in stream [%d]\n", primeSrc.getPrimeRefStream(startIdx, useParallel).findFirst().get().getPrime());
			final Gson gson = new GsonBuilder().setExclusionStrategies(excludes).serializeNulls().create();
			result.setResult(
					gson.toJson(
						primeSrc
						.getPrimeRefStream(startIdx, useParallel)
						.limit(maxIndexes)
						.<JsonData>map(pRef -> new JsonData(
								
								pRef.getPrimeRefIdx(),
								
								pRef.getPrime(),
								
								baseType != null
								? pRef
									.getPrimeBases(BASE_TYPES.select(base -> base.name().equals(baseType)).getOnly())
								: EMPTY_ARRAY,	
								
								baseType != null
									? Arrays.stream(pRef
										.getPrimeBases(BASE_TYPES.select(base -> base.name().equals(baseType)).getOnly()))
										.anyMatch(anyBasePred) 
										|| entireBasePred.test(LongLists.immutable.of(pRef.getPrimeBases(BASE_TYPES.select(base -> base.name().equals(baseType)).getOnly())))
									: true))
					
						.filter(json -> baseType == null || json.isKeep())
						.toArray()));
		}
		catch(final Exception e)
		{
			System.out.println("*** Json output exception " + e.toString());
			e.printStackTrace();
		}
	}
}
