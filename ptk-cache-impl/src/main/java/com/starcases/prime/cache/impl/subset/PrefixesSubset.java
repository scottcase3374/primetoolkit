package com.starcases.prime.cache.impl.subset;

import java.lang.reflect.Array;

import com.starcases.prime.cache.api.subset.PrefixSubsetIntfc;

import lombok.Getter;
import lombok.NonNull;

/**
 * Container with configurable size for holding multiple prime/base items
 * as a group for storage in cache/external storage. Per item storage is
 * high at large scale so bundling items reduces the overhead.
 */

public class PrefixesSubset implements PrefixSubsetIntfc
{
	/**
	 * Array of entries bundled together
	 */
	@Getter
	protected Long [][] entries;

	@Getter
	protected int maxOffsetAssigned = -1;

	/**
	 * Constructor for the container used for bundling prime/bases.
	 *
	 *
	 * @param wrapper Bases to take ownership of.
	 */
	public PrefixesSubset(@NonNull final Long[][] wrapper)
	{
		this.entries = wrapper;
		this.maxOffsetAssigned = entries.length-1;
	}

	/**
	 * Constructor for the container used for bundling prime/bases.
	 * @param entries
	 */
	public PrefixesSubset(final int count)
	{
		alloc(count);
	}

	/**
	 * Method for runtime allocation of the desired container size.
	 * @param subsetSize
	 */
	@Override
	public void alloc(final int subsetSize)
	{
		entries = (Long[][])Array.newInstance(Long.class, subsetSize);
	}

	/**
	 * Assign prime/base to location in container
	 *
	 * @param offset
	 * @param val
	 */
	@Override
	public void set(final int offset, final Long[] val)
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
	public Long[] get(final int offset)
	{
		Long[] ret = null;
		if (offset <= maxOffsetAssigned)
		{
			ret = entries[offset];
		}
		return ret;
	}

	@Override
	public String toString()
	{
		return String.format("entry-storage:[%d] max-offset-assigned[%d]", entries.length, maxOffsetAssigned);
	}

	/**
	 * Returns count of entries-1
	 */
	@Override
	public int getMaxOffset()
	{
		return entries.length-1;
	}
}
