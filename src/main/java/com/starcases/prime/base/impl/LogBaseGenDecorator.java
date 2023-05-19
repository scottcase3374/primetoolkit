package com.starcases.prime.base.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.api.PrimeBaseGeneratorIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;

import lombok.NonNull;

public class LogBaseGenDecorator implements PrimeBaseGeneratorIntfc
{
	private static final Logger LOG = Logger.getLogger(LogBaseGenDecorator.class.getName());

	private final PrimeBaseGeneratorIntfc generator;

	public LogBaseGenDecorator(@NonNull final PrimeBaseGeneratorIntfc baseGenerator)
	{
		this.generator = baseGenerator;
	}

	@Override
	public void genBasesForPrimeRef(@NonNull final PrimeRefIntfc curPrime)
	{
		if(LOG.isLoggable(Level.FINE))
		{
			LOG.fine(String.format("Base generation (%s) prime [%d] index [%d] ",
					getBaseType().name(),
					curPrime.getPrime(),
					curPrime.getPrimeRefIdx()));
		}

		generator.genBasesForPrimeRef(curPrime);
	}

	@Override
	public BaseTypes getBaseType()
	{
		return generator.getBaseType();
	}
}
