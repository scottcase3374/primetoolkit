package com.starcases.prime.intfc;

import com.starcases.prime.preload.PrePrimed;

/**
 * Private interface used by factory. Any prime source implementation
 * must implement this interface - it provides a way to convey
 * any pre-loaded prime data into a prime source instance.
 * The result of completing all the factory processing is an
 * instance where the prime source is handled as a the more
 * generic PrimeSourceIntfc which prevents changing the
 * preprimed reference.
 *
 * This is an alternative to simply using another constructor
 * argument.
 *
 * @author scott
 *
 */
public interface PrimeSourceFactoryIntfc extends PrimeSourceIntfc
{
	void setPrePrimed(final PrePrimed prePrimed);
}
