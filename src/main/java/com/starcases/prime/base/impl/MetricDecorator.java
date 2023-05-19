package com.starcases.prime.base.impl;

import java.util.Optional;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.api.PrimeBaseGeneratorIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.metrics.MetricMonitor;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.LongTaskTimer.Sample;
import lombok.NonNull;

public class MetricDecorator implements PrimeBaseGeneratorIntfc
{
	private final PrimeBaseGeneratorIntfc generator;

	public MetricDecorator(@NonNull final PrimeBaseGeneratorIntfc baseGenerator)
	{
		this.generator = baseGenerator;
	}

	@Override
	public void genBasesForPrimeRef(@NonNull final PrimeRefIntfc curPrime)
	{
		final Optional<LongTaskTimer.Sample> timer = MetricMonitor.longTimer(getBaseType());
		try
		{
			generator.genBasesForPrimeRef(curPrime);
		}
		finally
		{
			timer.ifPresent(Sample::stop);
		}
	}

	@Override
	public BaseTypes getBaseType()
	{
		return generator.getBaseType();
	}
}
