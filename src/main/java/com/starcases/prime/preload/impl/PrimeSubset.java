package com.starcases.prime.preload.impl;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import lombok.Getter;

/**
 * Container with configurable size for holding multiple prime/base items
 * as a group for storage in cache/external storage. Per item storage is
 * high at large scale so bundling items reduces the overhead.
 */

public class PrimeSubset
{
	/**
	 * Array of entries bundled together
	 */
	@Getter
	@ProtoField(number = 1)
	protected long [] entries;

	/**
	 * Constructor for the container used for bundling prime/bases.
	 * @param entries
	 */
	@ProtoFactory
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
	public void set(final int offset, final long val)
	{
		entries[offset] = val;
	}

	/**
	 * retrieve prime/base from container
	 * @param offset
	 * @return
	 */
	public long get(final int offset)
	{
		return entries[offset];
	}
}
