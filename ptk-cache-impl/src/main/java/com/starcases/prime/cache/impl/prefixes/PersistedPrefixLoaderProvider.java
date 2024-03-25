package com.starcases.prime.cache.impl.prefixes;

import java.nio.file.Path;
import javax.cache.Cache;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.cache.api.subset.PrefixSubsetIntfc;
import com.starcases.prime.cache.api.subset.PrefixSubsetProviderIntfc;
import com.starcases.prime.cache.api.persistload.PersistLoaderIntfc;
import com.starcases.prime.cache.api.persistload.PersistPrefixLoaderProviderIntfc;
import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class PersistedPrefixLoaderProvider implements PersistPrefixLoaderProviderIntfc
{
	/**
	 * default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("PERSISTLOADER", "PREFIXES");

	@Override
	public PersistLoaderIntfc create(@NonNull Cache<Long, PrefixSubsetIntfc> cache, @NonNull Path path,
			@NonNull PrefixSubsetProviderIntfc subsetProvider, ImmutableMap<String, Object> settings)
	{
		return new PersistedPrefixLoaderImpl(cache, path, subsetProvider);
	}

	/**
	 * get provider attributes.
	 */
	@Override
	public ImmutableCollection<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
