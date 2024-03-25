package com.starcases.prime.core.api;

import com.starcases.prime.base.api.BaseGenIntfc;

/** interface used by factory. Any prime source implementation
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
	void addBaseGenerator(final BaseGenIntfc baseGenerator);

	/**
	 * DefaultInit base info.
	 */
	void init();
}
