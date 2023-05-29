package com.starcases.prime.base.impl;

import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.api.PrimeBaseGeneratorIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.impl.PTKLogger;

import lombok.NonNull;

public class LogBaseGenDecorator implements PrimeBaseGeneratorIntfc
{
	private final PrimeBaseGeneratorIntfc generator;

	public LogBaseGenDecorator(@NonNull final PrimeBaseGeneratorIntfc baseGenerator)
	{
		this.generator = baseGenerator;
	}

	  @Override
	  public void genBasesForPrimeRef(@NonNull final PrimeRefIntfc curPrime)
	  {
		  PTKLogger.output("Base generation (%s) prime [%d] index [%d]%n",
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


	@Override
	public PrimeBaseGeneratorIntfc assignPrimeSrc(PrimeSourceIntfc primeSrc)
	{
		return null;
	}

}
