package com.starcases.prime.sql.api;

import java.util.function.Predicate;
import com.starcases.prime.core.api.PrimeRefIntfc;
import org.eclipse.collections.api.block.predicate.primitive.LongPredicate;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.list.ImmutableList;

public interface OutputServiceIntfc
{
	void output(
			final String baseType
			,final long startIdx
			,final long maxIndexes
			,final boolean useParallel
			,final Predicate<? super PrimeRefIntfc> idxFilter
			,final LongPredicate baseFilter
			,final ImmutableList<String> excludeFields
			);
}
