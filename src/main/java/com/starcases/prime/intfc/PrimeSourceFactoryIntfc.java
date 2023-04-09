package com.starcases.prime.intfc;

import com.starcases.prime.impl.GenerationProgress;
import com.starcases.prime.preload.PrePrimed;

/**
 * Private interface used by factory. Any prime source implementation
 * must implement this interface.
 *
 * Provides a way to convey
 * any pre-loaded prime data into a prime source instance.
 * The result of completing all the factory processing is an
 * instance where the prime source is handled as a the more
 * generic PrimeSourceIntfc which prevents changing the
 * preprimed reference.
 *
 * Other methods only needed during the prime/base construction
 * should reside here as well.
 *
 * This is an alternative to simply using more constructor
 * arguments.
 *
 * @author scott
 *
 */
public interface PrimeSourceFactoryIntfc extends PrimeSourceIntfc
{
	void setPrePrimed(final PrePrimed prePrimed);

	/**
	 * output progress of initial base creation.
	 * @param doDisplay
	 */
	void setDisplayProgress(GenerationProgress progress);

	/**
	 * set flag indicating whether to output metrics regarding the
	 * prime trees generated for initial base.
	 * @param doDisplay
	 */
	void setDisplayPrimeTreeMetrics(boolean doDisplay);

	/**
	 * DefaultInit base info.
	 */
	void init();
}
