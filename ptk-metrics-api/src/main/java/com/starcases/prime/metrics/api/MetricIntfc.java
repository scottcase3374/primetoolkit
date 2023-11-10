package com.starcases.prime.metrics.api;

import lombok.NonNull;

public interface MetricIntfc extends AutoCloseable
{
	void recordInfo(@NonNull final Runnable fn);
}
