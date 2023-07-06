package com.starcases.prime.base.impl;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.base.api.BaseGenIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.NonNull;

public class LogBaseGenDecor implements BaseGenIntfc
{
	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();

	private final BaseGenIntfc generator;

	public LogBaseGenDecor(@NonNull final BaseGenIntfc baseGenerator)
	{
		this.generator = baseGenerator;
	}

	  @Override
	  public void genBasesForPrimeRef(@NonNull final PrimeRefIntfc curPrime)
	  {
		  statusHandler.output(generator.getBaseType(), "Base gen: prime [%d] index [%d]%n",
					  getBaseType().name(),
					  curPrime.getPrime(),
					  curPrime.getPrimeRefIdx());

		  generator.genBasesForPrimeRef(curPrime);
	  }

	  @Override
	  public BaseTypesIntfc getBaseType()
	  {
		  return generator.getBaseType();
	  }
}
