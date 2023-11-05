package com.starcases.prime.sql.csvoutput.impl;

import java.io.StringWriter;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.collections.api.list.ImmutableList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.impl.factory.Lists;

import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.service.impl.SvcLoader;
import com.starcases.prime.sql.api.OutputServiceIntfc;
import com.starcases.prime.sql.api.PrimeSqlResultIntfc;

import lombok.NonNull;

public class CSVOutputSvcImpl implements OutputServiceIntfc
{
	private static final String FIELD_INDEX = "index";
	private static final String FIELD_PRIME = "prime";
	private static final String FIELD_BASE = "base";

	private static final Object[] EMPTY_ARRAY = {};
	private static final ImmutableList<BaseTypesIntfc> BASE_TYPES =
			new SvcLoader<BaseTypesProviderIntfc, Class<BaseTypesProviderIntfc>>(BaseTypesProviderIntfc.class)
				.provider( Lists.immutable.of("GLOBAL_BASE_TYPES"))
				.orElseThrow()
				.create();

	private PrimeSourceIntfc primeSrc;
	private PrimeSqlResultIntfc result;

	public CSVOutputSvcImpl()
	{
	}

	public OutputServiceIntfc init(final PrimeSourceIntfc primeSrc, final PrimeSqlResultIntfc result)
	{
		this.primeSrc = primeSrc;
		this.result = result;
		return this;
	}

	@Override
	public void output(	final String baseType,
						final long startIdx,
						final long maxIndexes,
						final boolean useParallel,
						@NonNull final Predicate<? super PrimeRefIntfc> idxFilter,
						@NonNull final Predicate<? super ImmutableLongCollection> baseFilter,
						final ImmutableList<String> excludeFields
						)
	{
		final var sWriter = new StringWriter();
		try(CSVPrinter printer = new CSVPrinter(sWriter, CSVFormat.DEFAULT))
		{
			final Stream.Builder<String> strHrdBuilder = Stream.builder();
			if (!excludeFields.contains(FIELD_INDEX))
			{
				strHrdBuilder.add(FIELD_INDEX);
			}

			if (!excludeFields.contains(FIELD_PRIME))
			{
				strHrdBuilder.add(FIELD_PRIME);
			}

			if (!excludeFields.contains(FIELD_BASE))
			{

			}
			printer.printRecord(strHrdBuilder.build());

				  primeSrc
				  	.getPrimeRefStream(startIdx, useParallel)
				  	.limit(maxIndexes)
				  	.<CSVData>map(pRef -> new CSVData( pRef.getPrimeRefIdx(), pRef.getPrime(),
				  			baseType != null ?
				  					pRef.getPrimeBaseData()
				  					.getPrimeBases(BASE_TYPES.select(base -> base.name().equals(baseType)).getOnly())
				  					.stream()
				  					// Filter tuples out of bases for each matched prime which where tuple doesn't meet the match criteria
				  					.filter(baseFilter)
				  					.map(lc -> lc.toArray())
				  					.toArray()
				  				: EMPTY_ARRAY))
				  				.forEach(p -> {
				  							try
				  							{
				  								final Stream.Builder<Object> streamBuilder = Stream.builder();

				  								if (!excludeFields.contains(FIELD_INDEX))
				  								{
				  									streamBuilder.add(p.index);
				  								}

				  								if (!excludeFields.contains(FIELD_PRIME))
				  								{
				  									streamBuilder.add(p.prime);
				  								}

				  								if (!excludeFields.contains(FIELD_BASE))
				  								{

				  								}

				  								printer.printRecord(streamBuilder.build());
				  							}
				  							catch(final Exception e)
				  							{}
				  						  });

				result.setResult(sWriter.toString());
		}
		catch(final Exception e)
		{
				result.setResult(e.toString());
				result.setError(e.toString());
		}
	}
}
