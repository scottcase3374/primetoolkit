package com.starcases.prime.base.impl;

import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.api.BaseGenIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.impl.PTKLogger;

import lombok.NonNull;

public class LogBaseGenDecor implements BaseGenIntfc
{
	private final BaseGenIntfc generator;

	public LogBaseGenDecor(@NonNull final BaseGenIntfc baseGenerator)
	{
		this.generator = baseGenerator;
	}

	  @Override
	  public void genBasesForPrimeRef(@NonNull final PrimeRefIntfc curPrime)
	  {
		  System.out.println("base gen logger");
		  PTKLogger.output(generator.getBaseType(), "Base gen: prime [%d] index [%d]%n",
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
