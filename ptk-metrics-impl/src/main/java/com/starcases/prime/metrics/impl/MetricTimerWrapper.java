package com.starcases.prime.metrics.impl;

import com.starcases.prime.metrics.api.MetricIntfc;

import io.micrometer.core.instrument.Timer;
import lombok.NonNull;

public class MetricTimerWrapper implements MetricIntfc
{
	private final Timer timer;

	public MetricTimerWrapper(@NonNull final Timer timer)
	{
		this.timer = timer;
	}

	public void record(@NonNull final Runnable fn)
	{
		fn.run();
	}

	@Override
	public void close() throws Exception
	{
		timer.close();
	}
}
