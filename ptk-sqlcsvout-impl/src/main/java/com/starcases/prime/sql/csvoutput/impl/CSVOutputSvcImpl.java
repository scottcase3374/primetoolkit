package com.starcases.prime.sql.csvoutput.impl;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.collections.api.list.ImmutableList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.collections.api.block.predicate.primitive.LongPredicate;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.Lists;

import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.kern.api.PtkException;
import com.starcases.prime.service.impl.SvcLoader;
import com.starcases.prime.sql.api.OutputServiceIntfc;
import com.starcases.prime.sql.api.PrimeResultIntfc;

import lombok.NonNull;

public class CSVOutputSvcImpl implements OutputServiceIntfc
{
	private static final String FIELD_INDEX = "index";
	private static final String FIELD_PRIME = "prime";
	private static final String FIELD_BASE = "base";

	private static final long[] EMPTY_ARRAY = {};
	private static final ImmutableList<BaseTypesIntfc> BASE_TYPES =
			new SvcLoader<BaseTypesProviderIntfc, Class<BaseTypesProviderIntfc>>(BaseTypesProviderIntfc.class)
				.provider( Lists.immutable.of("GLOBAL_BASE_TYPES"))
				.orElseThrow()
				.create();

	private PrimeSourceIntfc primeSrc;
	private PrimeResultIntfc result;

	public CSVOutputSvcImpl()
	{
		/* nothing to do */
	}

	public OutputServiceIntfc init(final PrimeSourceIntfc primeSrc, final PrimeResultIntfc result)
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
						@NonNull final LongPredicate baseFilter,
						@NonNull final Predicate<? super ImmutableLongCollection> entireBasePred,
						final ImmutableList<String> excludeFields
						)
	{
		final var sWriter = new StringWriter();
		final BaseTypesIntfc selectedBase = baseType != null ? BASE_TYPES.select(base -> base.name().equals(baseType)).getOnly() : null;
		try(CSVPrinter printer = new CSVPrinter(sWriter, CSVFormat.DEFAULT))
		{
			final Stream.Builder<String> strHdrBuilder = Stream.builder();
			if (!excludeFields.contains(FIELD_INDEX))
			{
				strHdrBuilder.add(FIELD_INDEX);
			}

			if (!excludeFields.contains(FIELD_PRIME))
			{
				strHdrBuilder.add(FIELD_PRIME);
			}

			if (!excludeFields.contains(FIELD_BASE))
			{
				strHdrBuilder.add(FIELD_BASE);
			}
			printer.printRecord(strHdrBuilder.build());

				  primeSrc
				  	.getPrimeRefStream(startIdx, useParallel)
				  	.limit(maxIndexes)
				  	.filter(pRef -> selectedBase == null ||
		  					Arrays.stream(getPrimeBases(selectedBase, pRef)).anyMatch(baseFilter)
		  					|| entireBasePred.test(LongLists.immutable.of(getPrimeBases(selectedBase, pRef)))
		  				)

				  	.<CSVData>map(pRef -> new CSVData(
				  			pRef.getPrimeRefIdx(),
				  			pRef.getPrime(),
				  			getPrimeBases(selectedBase, pRef),
				  			true))
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
				  									final StringBuilder bases = new StringBuilder();
				  									Arrays.stream(p.base)
				  											.forEach(l ->
				  														{
				  															if (!bases.isEmpty())
				  															{
				  																bases.append(":");
				  															}
				  															bases.append(Long.toString(l));
				  														}
				  											);
				  									streamBuilder.add(bases.toString());
				  								}

				  								printer.printRecord(streamBuilder.build());
				  							}
				  							catch(final Exception e)
				  							{
				  								throw new PtkException(e);
				  							}
				  						  });

				result.setResult(sWriter.toString());
		}
		catch(final Exception e)
		{
				result.setResult(e.toString());
				result.setError(e.toString());
		}
	}

	private long[] getPrimeBases(final BaseTypesIntfc baseType, final PrimeRefIntfc pRef)
	{
		long [] bases = baseType != null ?  pRef.getPrimeBases(baseType) : EMPTY_ARRAY;
		if (null == bases)
		{
			bases = EMPTY_ARRAY;
		}
		return bases;
	}
}
