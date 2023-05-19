package com.starcases.prime.base.impl;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.api.PrimeBaseGeneratorIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;

import lombok.NonNull;

public class LogTimerDecorator implements PrimeBaseGeneratorIntfc
{
	private static final Logger LOG = Logger.getLogger(LogTimerDecorator.class.getName());

	private final PrimeBaseGeneratorIntfc generator;

	public LogTimerDecorator(@NonNull final PrimeBaseGeneratorIntfc baseGenerator)
	{
		this.generator = baseGenerator;
	}

	@Override
	public void genBasesForPrimeRef(@NonNull final PrimeRefIntfc curPrime)
	{
		final Instant start = Instant.now();

		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("Base generation (%s) start time: %s", getBaseType(), start.toString()));
		}

		generator.genBasesForPrimeRef(curPrime);

		if (LOG.isLoggable(Level.INFO))
		{
			final var end = Instant.now();
			LOG.info(String.format("Base generation (%s) end time: %s", getBaseType(), end.toString()));
			final var diff = ChronoUnit.MILLIS.between(start, end);
			final var milliRemain = diff % 1_000;
			final var secondRemain = diff / 1_000 % 60;
			final var minuteRemain = diff / 60_000 % 60;

			final var timeDisplayFmt = new DecimalFormat("###,###");
			LOG.info(String.format("Base generation (%s): %s min %s sec %s milli",
					getBaseType().name(),
					timeDisplayFmt.format(minuteRemain),
					timeDisplayFmt.format(secondRemain),
					timeDisplayFmt.format(milliRemain)
					));
		}
	}

	@Override
	public BaseTypes getBaseType()
	{
		return generator.getBaseType();
	}
}
