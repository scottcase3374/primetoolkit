package com.starcases.prime.service;

import java.util.Iterator;
import java.util.ServiceLoader;
import com.starcases.prime.service_api.SvcProviderBaseIntfc;

public class SvcLoader< T1 extends SvcProviderBaseIntfc, T2 extends Class<T1>>
{
    private final ServiceLoader<T1> loader;

    public SvcLoader(final T2 classT)
    {
    	this.loader = ServiceLoader.load(classT);
    }

  public T1 provider(final String [] attributes)
  {
	  final Iterator<T1> it = loader.iterator();
	  T1 ret = null;
      while (it.hasNext())
      {
    	  T1 tmp = it.next();
    	  if (tmp.countAttributesMatch(attributes) > 0)
    	  {
    		  ret = tmp;
    	  }
      }
      return ret;
  }
}
