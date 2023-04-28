package com.starcases.prime.service;

import java.util.ServiceLoader;

import org.eclipse.collections.api.collection.ImmutableCollection;

import com.starcases.prime.service_api.SvcProviderBaseIntfc;

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
    public T provider(@NonNull final ImmutableCollection<String> attributes)
    {
	  return loader
			  .stream()
			  .max((x,y) -> x.get().countAttributesMatch(attributes) - y.get().countAttributesMatch(attributes) )
			  .get()
			  .get();
    }
}
