package com.starcases.prime.intfc;

import com.starcases.prime.preload.PrePrimed;

/**
 * Private interface used by factory
 * @author scott
 *
 */
public interface PrimeSourceFactoryIntfc extends PrimeSourceIntfc
{
	void setPrePrimed(final PrePrimed prePrimed);
}
