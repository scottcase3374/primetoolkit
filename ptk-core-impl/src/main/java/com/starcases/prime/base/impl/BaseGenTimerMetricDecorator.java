package com.starcases.prime.base.impl;

import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.api.PrimeBaseGeneratorIntfc;
import com.starcases.prime.core.api.OutputableIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.metrics.api.MetricIntfc;
import com.starcases.prime.metrics.api.MetricProviderIntfc;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class BaseGenTimerMetricDecorator  implements PrimeBaseGeneratorIntfc
{
	private final PrimeBaseGeneratorIntfc generator;
	private static final OutputableIntfc outputable = null;
	private final MetricProviderIntfc metricProvider;

	public BaseGenTimerMetricDecorator(@NonNull final PrimeBaseGeneratorIntfc baseGenerator, @NonNull final MetricProviderIntfc metricProvider)
	{
		this.generator = baseGenerator;
		this.metricProvider = metricProvider;
	}

	/**
	 * Wrapper bases generation with metric timer
	 */
	@Override
	public void genBasesForPrimeRef(@NonNull final PrimeRefIntfc curPrime)
	{
		try (MetricIntfc metric = metricProvider.timer(outputable, "BASES_GEN"))
		{
			metric.record(() -> generator.genBasesForPrimeRef(curPrime));
		}
		catch(final Exception e)
		{
			// TODO Review/update error handling.
			throw new RuntimeException(e);
		}
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
