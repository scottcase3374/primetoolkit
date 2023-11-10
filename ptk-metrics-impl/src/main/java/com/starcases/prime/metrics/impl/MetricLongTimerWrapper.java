package com.starcases.prime.metrics.impl;

import com.starcases.prime.metrics.api.MetricIntfc;

import io.micrometer.core.instrument.LongTaskTimer;
import lombok.NonNull;

class MetricLongTimerWrapper implements MetricIntfc
{
	private final LongTaskTimer.Sample longTimerSample;

	public MetricLongTimerWrapper(@NonNull final LongTaskTimer.Sample longTimerSample)
	{
		this.longTimerSample = longTimerSample;
	}

	@Override
	public void close() throws Exception
	{
		longTimerSample.stop();
	}

	@Override
	public void recordInfo(@NonNull Runnable fn)
	{
		fn.run();
	}
}
