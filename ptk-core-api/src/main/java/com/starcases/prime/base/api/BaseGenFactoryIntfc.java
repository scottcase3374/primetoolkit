package com.starcases.prime.base.api;

import com.starcases.prime.core.api.PrimeSourceIntfc;

/**
 *
 * Main interface for generating bases. This is implemented for
 * any alternative base types.
 *
 */
public interface BaseGenFactoryIntfc extends BaseGenIntfc
{
	BaseGenFactoryIntfc assignPrimeSrc(final PrimeSourceIntfc primeSrc);
	BaseGenFactoryIntfc doPreferParallel(final boolean preferParallel);
}
