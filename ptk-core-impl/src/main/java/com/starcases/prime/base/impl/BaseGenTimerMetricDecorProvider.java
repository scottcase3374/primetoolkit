package com.starcases.prime.base.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.base.api.BaseGenDecorProviderIntfc;

import com.starcases.prime.base.api.BaseGenIntfc;
import com.starcases.prime.metrics.api.MetricProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.NonNull;

public class BaseGenTimerMetricDecorProvider implements BaseGenDecorProviderIntfc
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("METRIC_BASE_GENERATOR_DECORATOR");

	private static final ImmutableList<String> METRIC_ATTRIBUTES = Lists.immutable.of("METRIC_PROVIDER");

	private static final MetricProviderIntfc metricProvider = new SvcLoader<MetricProviderIntfc, Class<MetricProviderIntfc>>(MetricProviderIntfc.class).provider(METRIC_ATTRIBUTES).get();

	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}

	@Override
	public BaseGenIntfc create(@NonNull final BaseGenIntfc baseGen)
	{
		return new BaseGenTimerMetricDecor(baseGen, metricProvider);
	}


}
