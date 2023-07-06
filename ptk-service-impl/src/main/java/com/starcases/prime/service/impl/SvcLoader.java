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

    	if (loader.findFirst().isEmpty())
    	{
    		logAccess(classT);
    	}
    }

    /**
     * Constructor when more dependencies are required for service provider construction.
     *
     * @param classT
     */
    public SvcLoader(@NonNull final C classT, @NonNull final Class<?> [] classesUsed, @NonNull final Module [] modulesToRead)
    {
    	logAccess(classT);
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

    	this.loader = ServiceLoader.load(classT);

    	if (loader.findFirst().isEmpty())
    	{
    		logAccess(classT);
    	}
    }


    private void logAccess(@NonNull final C classT)
    {
    	final Module module = this.getClass().getModule();
    	module.addReads(classT.getModule());

    	final Module classTModule = classT.getModule();

    	SvcProviderBaseIntfc.LOG.warning(String.format("[NOT FOUND] svc-loader: %n\t can read mod: [%s] [%b] %n\t can use classT: [%s] [%b] %n\tProvider: [%s]",
    			classTModule.getName(),
    			module.canRead(classTModule),
    			classT.getName(),
    			module.canUse(classT),
    			classT.getName()));
    }

    /**
     * Find BEST match (most matching attributes)
     * @param attributes
     * @return
     */
    public Optional<T> provider(@NonNull final ImmutableCollection<String> attributes)
    {
	  Optional<T> result =
			 providers(attributes)
			  .stream()
			  .max((x,y) -> Integer.compare(x.countAttributesMatch(attributes), y.countAttributesMatch(attributes)) );
	  return result;
    }

    /**
     * Find any minimal matching providers
     * @param attributes
     * @return
     */
    public ImmutableList<T> providers(@NonNull final ImmutableCollection<String> attributes)
    {
    	ImmutableList<T> result =  Lists.immutable.fromStream(
    		loader
			  .stream()
			  .peek(p -> SvcProviderBaseIntfc.LOG.info(String.format("Svcloader - requested attrs: [%s] provider-attributes: [%s] provider: [%s]", attributes.makeString(), p.get().getProviderAttributes().makeString(), p.get().getClass().getName())))
			  .filter(x -> { var p = x.get(); return p.countAttributesMatch(attributes) >= Math.min(attributes.size(), p.getProviderAttributes().size()); })
			  .map(p -> p.get())
			);

    	if (result == null || result.size() == 0)
    	{
    		System.out.println("ERROR: SvcLoader providers() - no matches for provided attributes: " + attributes.makeString());
    	}

    	return result;
    }
}
