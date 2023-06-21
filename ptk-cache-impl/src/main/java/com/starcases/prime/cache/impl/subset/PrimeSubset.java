package com.starcases.prime.cache.impl.subset;

import com.starcases.prime.cache.api.subset.PrimeSubsetIntfc;

import lombok.Getter;

/**
 * Container with configurable size for holding multiple prime/base items
 * as a group for storage in cache/external storage. Per item storage is
 * high at large scale so bundling items reduces the overhead.
 */

public class PrimeSubset implements PrimeSubsetIntfc
{
	/**
	 * Array of entries bundled together
	 */
	@Getter
	protected long [] entries;


	@Getter
	protected int maxOffsetAssigned;

	/**
	 * Constructor for the container used for bundling prime/bases.
	 * @param entries
	 */
	public PrimeSubset(final int count, final long ... entries)
	{
		this.maxOffsetAssigned = count;
		this.entries = entries.clone();
	}

	/**
	 * Constructor for the container used for bundling prime/bases.
	 * @param entries
	 */
	public PrimeSubset(final long ... entries)
	{
		this.entries = entries.clone();
	}

	/**
	 * No arg constructor to appease code-analysis/metrics
	 */
	public PrimeSubset()
	{}

	/**
	 * Method for runtime allocation of the desired container size.
	 * @param subsetSize
	 */
	@Override
	public void alloc(final int subsetSize)
	{
		entries = new long[subsetSize];
	}

	/**
	 * Assign prime/base to location in container
	 *
	 * @param offset
	 * @param val
	 */
	@Override
	public void set(final int offset, final long val)
	{
		entries[offset] = val;
		maxOffsetAssigned = offset;
	}

	/**
	 * retrieve prime/base from container
	 * @param offset
	 * @return
	 */
	@Override
	public long get(final int offset)
	{
		return entries[offset];
	}

	@Override
	public String toString()
	{
		return String.format("entry-storage:[%d] max-offset-assigned[%d]", entries.length, maxOffsetAssigned);
	}
}
