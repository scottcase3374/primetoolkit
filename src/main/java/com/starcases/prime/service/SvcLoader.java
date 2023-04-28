package com.starcases.prime.service;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.service_api.SvcProviderBaseIntfc;

@SuppressWarnings({"PMD.GenericsNaming"})
public class SvcLoader< T1 extends SvcProviderBaseIntfc, T2 extends Class<T1>>
{
    private final ServiceLoader<T1> loader;

    public SvcLoader(final T2 classT)
    {
    	this.loader = ServiceLoader.load(classT);
    }

  public T1 provider(final ImmutableList<String> attributes)
  {
	  final Iterator<T1> it = loader.iterator();
	  T1 ret = null;
      while (it.hasNext())
      {
    	  final T1 tmp = it.next();
    	  if (tmp.countAttributesMatch(attributes) > 0)
    	  {
    		  ret = tmp;
    	  }
      }
      return ret;
  }
}
