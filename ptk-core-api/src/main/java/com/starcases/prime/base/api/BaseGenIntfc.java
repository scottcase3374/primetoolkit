package com.starcases.prime.base.api;

import com.starcases.prime.core.api.PrimeRefIntfc;

/**
 *
 * Main interface for generating bases. This is implemented for
 * any alternative base types.
 *
 */
public interface BaseGenIntfc
{
	void genBasesForPrimeRef(final PrimeRefIntfc curPrime);
	BaseTypesIntfc getBaseType();
}
