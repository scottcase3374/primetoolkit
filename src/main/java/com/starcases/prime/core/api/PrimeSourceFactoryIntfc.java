package com.starcases.prime.core.api;

import com.starcases.prime.base.api.PrimeBaseGeneratorIntfc;

/**
 * Private interface used by factory. Any prime source implementation
 * must implement this interface.
 *
 * The result of completing all the factory processing is an
 * instance where the prime source is handled as a the more
 * generic PrimeSourceIntfc which prevents changing internal
 * members that are intended as implementation details.
 *
 * Methods only needed during the prime/base construction
 * should reside here.
 *
 * This is an alternative to simply using more constructor
 * arguments.
 *
 * @author scott
 *
 */
public interface PrimeSourceFactoryIntfc extends PrimeSourceIntfc
{
	void addBaseGenerator(final PrimeBaseGeneratorIntfc baseGenerator);

	/**
	 * output progress of initial base creation.
	 * @param doDisplay
	 */
	void setDisplayProgress(final ProgressIntfc progress);

	/**
	 * set flag indicating whether to output metrics regarding the
	 * base data generated for initial base.
	 * @param doDisplay
	 */
	void setDisplayDefaultBaseMetrics(final boolean doDisplay);

	/**
	 * DefaultInit base info.
	 */
	void init();
}
