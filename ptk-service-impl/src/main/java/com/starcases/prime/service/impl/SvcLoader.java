package com.starcases.prime.service.impl;

import java.util.Optional;
import java.util.ServiceLoader;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

/**
 * Wrapper class which provides service loading.
 *
 * @author scott
 *
 * @param <T>
 * @param <C>
 */
public class SvcLoader< T extends SvcProviderBaseIntfc, C extends Class<T>>
{
    private final ServiceLoader<T> loader;

    /**
     * Constructor when no other dependencies are needed for service provider construction.
     *
     * @param classT
     */
    public SvcLoader(@NonNull final C classT)
    {
    	final Module module = this.getClass().getModule();
    	module.addReads(classT.getModule());
    	module.addUses(classT);
    	this.loader = ServiceLoader.load(classT);
    }

    /**
     * Constructor when more dependencies are required for service provider construction.
     *
     * @param classT
     */
    public SvcLoader(@NonNull final C classT, final Class<?> [] classesUsed, final Module [] modulesToRead)
    {
    	final Module module = this.getClass().getModule();
    	module.addReads(classT.getModule());
    	for (Module m : modulesToRead)
    	{
    		module.addReads(m);
    	}

    	module.addUses(classT);
    	for (Class<?> c : classesUsed)
    	{
    		module.addUses(c);
    	}

    	final Module classTModule = classT.getModule();

    	SvcProviderBaseIntfc.LOG.info(String.format("svc-loader: %n\t can read mod: [%s] [%b] %n\t can use classT: [%s] [%b] %n\tProvider: [%s]",
    			classTModule.getName(),
    			module.canRead(classTModule),
    			classT.getName(),
    			module.canUse(classT),
    			classT.getName()));

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
			  .peek(p -> SvcProviderBaseIntfc.LOG.info(String.format("Svcloader - requested attrs: [%s] provider-attributes: [%s] provider: [%s]", attributes.makeString(), p.get().getProviderAttributes().makeString(), p.get().getClass().getName())))
			  .filter(x -> { var p = x.get(); return p.countAttributesMatch(attributes) >= Math.min(attributes.size(), p.getProviderAttributes().size()); })
			  .map(p -> p.get())
			);
    }
}
