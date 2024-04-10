package com.starcases.prime.core.api;

import java.util.Iterator;
import java.util.stream.Stream;

import com.starcases.prime.base.api.BaseGenIntfc;

import jakarta.validation.constraints.Min;

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

	void setCreateBases(boolean createBases);

	/**
	 *
	 * @param nextPrimeIdx
	 * @param newPrime
	 * @param defaultBase
	 * @return
	 */
	PrimeRefFactoryIntfc addPrimeRef(
			@Min(0) final long nextPrimeIdx,
			@Min(1) final long newPrime
			);

	void generateBases(final PrimeRefFactoryIntfc pRef);

	/**
	 * Get a stream of prime refs and indicate whether parallel stream ops
	 * are allowed.
	 *
	 * @param preferParallel
	 * @return
	 */
	Stream<PrimeRefFactoryIntfc> getPrimeFactoryRefStream(boolean preferParallel);

	/**
	 * Get a stream of prime refs  after skipping
	 * an initial count; indicate whether parallel stream ops
	 * are allowed.
	 *
	 * @param skipCount
	 * @param preferParallel
	 * @return
	 */
	Stream<PrimeRefFactoryIntfc> getPrimeFactoryRefStream(long skipCount, boolean preferParallel);

	/**
	 * Iterator to PrimeRefIntfc instances.
	 *
	 * @return
	 */
	Iterator<PrimeRefFactoryIntfc> getPrimeFactoryRefIter();

	/**
	 * Iterator to PrimeRefIntfc instances.
	 *
	 * @return
	 */
	Iterator<PrimeRefFactoryIntfc> getPrimeFactoryRefIter(long startIdx);

}
