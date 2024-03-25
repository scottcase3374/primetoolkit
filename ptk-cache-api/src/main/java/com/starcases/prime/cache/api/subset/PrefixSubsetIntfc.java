package com.starcases.prime.cache.api.subset;

public interface PrefixSubsetIntfc
{

	Long[][] getEntries();

	int getMaxOffsetAssigned();

	int getMaxOffset();

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
	void set(final int offset, final Long[] val);

	/**
	 * retrieve prime/base from container
	 * @param offset
	 * @return
	 */
	Long[] get(final int offset);

	String toString();
}