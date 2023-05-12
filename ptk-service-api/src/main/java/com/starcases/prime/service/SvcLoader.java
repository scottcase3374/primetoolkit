package com.starcases.prime.service;

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
    	loader.forEach(System.out::println);
    }

    /**
     * Find BEST match (most matching attributes)
     * @param attributes
     * @return
     */
    public T provider(@NonNull final ImmutableCollection<String> attributes)
    {
	  return loader
			  .stream()
			  .max((x,y) -> Integer.compare(x.get().countAttributesMatch(attributes), y.get().countAttributesMatch(attributes)) )
			  .get()
			  .get();
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
