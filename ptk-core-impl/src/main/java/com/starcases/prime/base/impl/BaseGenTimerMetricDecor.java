package com.starcases.prime.base.impl;

import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.api.BaseGenIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.metrics.api.MetricIntfc;
import com.starcases.prime.metrics.api.MetricProviderIntfc;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class BaseGenTimerMetricDecor  implements BaseGenIntfc
{
	private final BaseGenIntfc generator;
	private final BaseTypesIntfc base;
	private final MetricProviderIntfc metricProvider;

	public BaseGenTimerMetricDecor(@NonNull final BaseGenIntfc baseGenerator, @NonNull final MetricProviderIntfc metricProvider)
	{
		this.generator = baseGenerator;
		this.metricProvider = metricProvider;
		this.base = baseGenerator.getBaseType();
	}

	/**
	 * Wrapper bases generation with metric timer
	 */
	@Override
	public void genBasesForPrimeRef(@NonNull final PrimeRefIntfc curPrime)
	{
		try (MetricIntfc metric = metricProvider.timer(base, "BASES_GEN", base.toString()))
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
}
