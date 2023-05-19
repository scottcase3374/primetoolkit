package com.starcases.prime.service;

import java.util.Optional;
import java.util.ServiceLoader;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public class SvcLoader< T extends SvcProviderBaseIntfc, C extends Class<T>>
{
    private final ServiceLoader<T> loader;

    public SvcLoader(final C classT)
    {
    	this.loader = ServiceLoader.load(classT);
    }

    /**
     * Find BEST match (most matching attributes)
     * @param attributes
     * @return
     */
    public Optional<T> provider(@NonNull final ImmutableCollection<String> attributes)
    {
	  return
			 providers(attributes)
			  .stream()
			  .max((x,y) -> Integer.compare(x.countAttributesMatch(attributes), y.countAttributesMatch(attributes)) )
			  ;
    }

    /**
     * Find any minimal matching providers
     * @param attributes
     * @return
     */
    public ImmutableList<T> providers(@NonNull final ImmutableCollection<String> attributes)
    {
    	return Lists.immutable.fromStream(
    		loader
			  .stream()
			  .filter(x -> x.get().countAttributesMatch(attributes) > 0 )
			  .map(p -> p.get())
			);
    }
}
