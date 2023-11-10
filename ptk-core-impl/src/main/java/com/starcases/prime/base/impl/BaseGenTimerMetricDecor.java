package com.starcases.prime.base.impl;

import java.util.logging.Level;

import org.eclipse.collections.api.factory.Lists;

import com.starcases.prime.base.api.BaseGenIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.metrics.api.MetricIntfc;
import com.starcases.prime.metrics.api.MetricProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public class BaseGenTimerMetricDecor  implements BaseGenIntfc
{

	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();

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
			metric.recordInfo(() -> generator.genBasesForPrimeRef(curPrime));
		}
		catch(final Exception e)
		{
			// Rethrows
			statusHandler.handleError(() -> "Problem generating bases for prime (with metric provider).", Level.SEVERE, e, true, true);
		}
	}

	@Override
	public BaseTypesIntfc getBaseType()
	{
		return generator.getBaseType();
	}
}
