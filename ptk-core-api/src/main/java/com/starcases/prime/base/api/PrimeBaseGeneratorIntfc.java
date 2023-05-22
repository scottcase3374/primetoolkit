package com.starcases.prime.base.api;

import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;

/**
 *
 * Main interface for generating bases. This is implemented for
 * any alternative base types.
 *
 */
public interface PrimeBaseGeneratorIntfc
{
	void genBasesForPrimeRef(final PrimeRefIntfc curPrime);
	BaseTypesIntfc getBaseType();

	PrimeBaseGeneratorIntfc assignPrimeSrc(final PrimeSourceIntfc primeSrc);
}
