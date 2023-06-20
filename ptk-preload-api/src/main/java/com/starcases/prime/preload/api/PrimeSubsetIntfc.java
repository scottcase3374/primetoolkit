package com.starcases.prime.preload.api;

public interface PrimeSubsetIntfc
{

	long[] getEntries();

	int getMaxOffsetAssigned();

	/**
	 * Method for runtime allocation of the desired container size.
	 * @param subsetSize
	 */
	void alloc(final int subsetSize);

	/**
	 * Assign prime/base to location in container
	 *
	 * @param offset
	 * @param val
	 */
	void set(final int offset, final long val);

	/**
	 * retrieve prime/base from container
	 * @param offset
	 * @return
	 */
	long get(final int offset);

	String toString();
}